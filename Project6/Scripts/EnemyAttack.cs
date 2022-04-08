using System.Collections;
using UnityEngine;

public class EnemyAttack : Attack
{
    private BoxCollider meleeCollider;
    private Enemy enemy;
    private Player player;

    private void Awake()
    {
        if (m_attackData.m_attackType == AttackType.Melee)
        {
            meleeCollider = GetComponent<BoxCollider>();
            Vector3 size = meleeCollider.size;
            meleeCollider.size = new Vector3(size.x, m_attackData.m_attackRange, size.z);
        }
    }

    protected override void Start()
    {
        base.Start();
        enemy = GetComponentInParent<Enemy>();
        player = GameObject.FindGameObjectWithTag("Player").GetComponent<Player>();
    }

    public override void DoAttack()
    {
        if (m_attackData.m_attackType == AttackType.Melee)
        {
            StartCoroutine("DelayedDamage", m_attackData.m_damage);
        } 
        else if (m_attackData.m_attackType == AttackType.Ranged)
        {
            StartCoroutine(DelayedBullet());
        }
        attackTimeoutDelta = m_attackData.m_attackTimeout;
    }

    public override void DoSecondary() { }

    IEnumerator DelayedDamage(float damage)
    {
        yield return new WaitForSeconds(m_attackData.m_dodgeWindow);
        Debug.Log("Skelly attacked");
        player.SendMessage("Damage", damage);
    }

    IEnumerator DelayedBullet()
    {
        yield return new WaitForSeconds(m_attackData.m_fireDelay);
        GameObject bullet = GameObject.Instantiate(m_attackData.m_projPrefab, m_attackData.m_attackOrigin.position, Quaternion.LookRotation(transform.forward));
        Projectile proj = bullet.GetComponent<Projectile>();
        proj.damage = m_attackData.m_damage;
        Vector3 pos = m_attackData.m_attackOrigin.position; // where attack starts from
        Vector3 dir = ((player.transform.position + new Vector3(0, 1.5f, 0)) - pos).normalized; // direction towards target from attack origin
        Vector3 target = pos + dir * m_attackData.m_attackRange; // vector from origin to target, for length of attacks range
        Vector3 predictedTarget = target + player.currentMovement; // use targets movement to predict where to shoot
        proj.target = predictedTarget;
    }
}