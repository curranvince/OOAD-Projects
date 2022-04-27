using System;
using System.Linq;
using System.Collections.Generic;
using UnityEngine;

public enum EnemyStateID
{
    Idle,
    Combat,
    Death
}

/* all states will inherit from enemy state */ 
public interface EnemyState
{
    EnemyStateID GetID();
    void Enter(EnemyController controller);
    void Update(EnemyController controller);
    void Exit(EnemyController controller);

}

/* idle state for when enemy has nothign to do */
public class IdleState : EnemyState
{
    private float _idleTime;

    public EnemyStateID GetID() => EnemyStateID.Idle;

    public void Enter(EnemyController controller)
    {
        _idleTime = 0;
    }

    /* TO DO : random movement, patrols ?*/
    public void Update(EnemyController controller)
    {
        /* waiting for idle time makes sure noone goes in combat state during loading */
        if (_idleTime >= 2.0f)
        {
            /* if player out of range do nothing */
            Vector3 playerDirection = controller.playerTransform.position - controller.transform.position;
            if (playerDirection.magnitude > controller.m_alertDistance) return;

            /* if player in range & vision then switch to combat state */
            Vector3 agentDirection = controller.transform.forward;
            playerDirection.Normalize();
            float dotProduct = Vector3.Dot(playerDirection, agentDirection);
            if (dotProduct > 0.0f && InLineOfSight(controller))
                controller.stateMachine.ChangeState(EnemyStateID.Combat);

            controller.animator.SetFloat("MoveZ", controller.agent.velocity.magnitude);
        } 

        _idleTime += Time.deltaTime;
    }

    /* returns if player is in enemies line of sight or not */
    private bool InLineOfSight(EnemyController controller)
    {
        /* create ray from enemies chest to players chest */
        Vector3 pos = controller.transform.position + new Vector3(0, 1.5f, 0);
        Vector3 dir = ((controller.playerTransform.position + new Vector3(0, 1.5f, 0)) - pos).normalized;
        /* if the ray connects with a part of the player, they can be seen */
        return (Physics.Raycast(pos, dir, out RaycastHit hit, controller.m_alertDistance) && hit.collider.transform.name == "PGeometry");
    }

    public void Exit(EnemyController controller)
    {
        // throw new System.NotImplementedException();
    }
}

/* state to handle combat */
public class CombatState : EnemyState
{
    /* timeout for updating path */
    private float _timeout = 1.5f;
    private float _timerDelta;
    /* timeout for combat */
    private float _combatTimeout = 10f;
    private float _combatTimeoutDelta;
    /* cache initial position */
    private float _initialX;
    private float _initialY;
    private float _initialZ;

    private int _weaponIndex;

    public EnemyStateID GetID() => EnemyStateID.Combat;
    public void Enter(EnemyController controller)
    {
        _timerDelta = _timeout;
        _combatTimeoutDelta = _combatTimeout;
        _initialX = controller.transform.position.x;
        _initialY = controller.transform.position.y;
        _initialZ = controller.transform.position.z;
        /* make agent stop when it gets w/in attack range */
        // controller.agent.stoppingDistance = GetAttack(controller).m_attackData.m_attackRange;
        if (controller.character is Boss)
        {
            Boss boss = controller.character as Boss;
            boss.SetHealthBar(true);
        }
    }

    /* TO DO : Choosing between attacks */
    public void Update(EnemyController controller)
    {
        /* cant do anything without a controller */
        if (!controller.enabled) return;

        /* reset anim triggers each frame */
        controller.animator.SetBool("Attack", false);

        bool inSight = InLineOfSight(controller);

        /* check if combat should continue */
        if ((DistanceToPlayer(controller) > controller.m_followDistance) || !inSight)
        {
            _combatTimeoutDelta -= Time.deltaTime;

            /* if player out of sight or range, try to get closer to them */
            if (_combatTimeoutDelta <= (_combatTimeout - GetAttack(controller).m_attackData.m_attackTimeout - 1f))
                controller.agent.stoppingDistance = 1;

            /* return to starting pos & reset health if player out of range or sight for too long */
            if (_combatTimeoutDelta <= 0)
            {
                controller.character.currentHealth = controller.character.m_maxHealth;
                controller.agent.destination = new Vector3(_initialX, _initialY, _initialZ);
                controller.stateMachine.ChangeState(EnemyStateID.Idle);
                return;
            }
        } else {
            _combatTimeoutDelta = _combatTimeout; // still in combat
        }

        /* set path immediately if agent has none */
        if (!controller.agent.hasPath)
            controller.agent.destination = controller.playerTransform.position;

        /* update destination somewhat sparingly since its expensive */
        if (_timerDelta < 0.0f || DistanceToPlayer(controller) < 3.0f)
        {
            controller.agent.destination = controller.playerTransform.position;
            _timerDelta = _timeout;
        }

        /* rotate to face the player */
        Vector3 dir = controller.playerTransform.position - controller.transform.position;
        Quaternion rot = Quaternion.LookRotation(dir);
        controller.transform.rotation = Quaternion.Slerp(controller.transform.rotation, rot, controller.character.m_rotationSpeed*Time.deltaTime);

        /* switch to 'optimal' attack based off which attacks range is closest to the actual distance from the player */
        if (controller.character.m_attacks.Count > 1)
        {
            int index = 0;
            float lowestDifference = 100f;
            for (int i = 0; i < controller.character.m_attacks.Count; i++)
            {
                float difference = controller.character.m_attacks[i].GetComponent<Attack>().m_attackData.m_attackRange - DistanceToPlayer(controller);
                // Debug.Log("Attack " + i + " has difference " + difference);
                if (difference >= 0 && difference < lowestDifference)
                {
                    index = i;
                    lowestDifference = difference;
                }
            }

            /*
            if (_weaponIndex == index && !GetAttack(controller).CanAttack())
            {
                IEnumerable<int> validIndex = Enumerable.Range(0, controller.character.m_attacks.Count - 1).Where(x => x != _weaponIndex);
                _weaponIndex = validIndex.ElementAt((int)Math.Round(d: UnityEngine.Random.Range(0, validIndex.Count() - 1)));
            }
            */
            
            if (_weaponIndex != index)
            {
                controller.character.SwitchToWeapon(index);
                _weaponIndex = index;
            }
        }
        
        /* try to do attacks */
        /* if player in range & in sight, stop moving */
        if (inSight && InRange(controller))
        {
            /* set stopping distance within range of target randomly for some spicier behavior */
            float currentRange = GetAttack(controller).m_attackData.m_attackRange;
            controller.agent.stoppingDistance = (GetAttack(controller).m_attackData.m_attackType == Attack.AttackType.Melee)
                ? UnityEngine.Random.Range(1.0f, currentRange)
                : UnityEngine.Random.Range((((currentRange - 10.0f) < 1.0f) ? currentRange : (currentRange - 10.0f)), currentRange);

            /* if attack is ready, use it */
            if (GetAttack(controller).CanAttack() && InFront(controller))
            {
                /*introduce a bit of randomness so attacks arent always instant when in range & ready */
                if (UnityEngine.Random.Range(0, 5) == 0)
                {
                    controller.animator.SetBool("Attack", true);
                    GetAttack(controller).DoAttack();
                }
            }
        }   

        /* set movement speed and tick timer */
        controller.animator.SetFloat("MoveZ", controller.agent.velocity.magnitude);
        _timerDelta -= Time.deltaTime;
    }

    /* get current distance to player */
    private float DistanceToPlayer(EnemyController controller) => (controller.playerTransform.position - controller.transform.position).magnitude;
    
    /* get current attack */
    private Attack GetAttack(EnemyController controller) => controller.character.attackObject;

    /* check if player in range of attack */
    private bool InRange(EnemyController controller) => DistanceToPlayer(controller) < GetAttack(controller).m_attackData.m_attackRange;

    /* check if player is in front of enemy (only checks angle) */
    private bool InFront(EnemyController controller)
    {
        Vector3 directionOfPlayer = controller.transform.position - controller.playerTransform.position;
        float angle = Vector3.Angle(controller.transform.forward, directionOfPlayer);

        return (Mathf.Abs(angle) > 90 && Mathf.Abs(angle) < 270);
    }

    /*
     * check if enemy can see player by
     * shooting ray from enemy chest to player chest 
     * return true if enemy can see player
    */
    private bool InLineOfSight(EnemyController controller)
    {
        Vector3 pos = controller.transform.position + new Vector3(0, 1.5f, 0); // pos of enemies 'eyes' (chest/neck)
        Vector3 dir = ((controller.playerTransform.position + new Vector3(0, 1.5f, 0)) - pos).normalized; // direction to players chest from our origin
        return (Physics.Raycast(pos, dir, out RaycastHit hit, GetAttack(controller).m_attackData.m_attackRange) && hit.collider.GetComponentInParent<Player>());
    }

    public void Exit(EnemyController controller)
    {
        // throw new System.NotImplementedException();
    }
}

/* state to handle death */
public class DeathState : EnemyState
{
    public EnemyStateID GetID() => EnemyStateID.Death;

    /* When entering death state play death effects & destroy gameobject */
    public void Enter(EnemyController controller)
    {
        /* instantiate death effects */ 
        if (controller.character.m_deathEffects.Length > 0)
        {
            foreach (var effect in controller.character.m_deathEffects)
                GameObject.Instantiate(effect, controller.transform.position, controller.transform.rotation);
        }
        /* ensure health bar doesnt 'hang around' while the objects being deleted */
        if (controller.character.healthBar) controller.character.healthBar.gameObject.SetActive(false);
        UnityEngine.Object.Destroy(controller.gameObject);
    }

    public void Update(EnemyController controller)
    {
        // throw new System.NotImplementedException();
    }

    public void Exit(EnemyController controller)
    {
        // throw new System.NotImplementedException();
    }
}