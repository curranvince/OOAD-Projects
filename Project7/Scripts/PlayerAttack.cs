using System.Collections;
using UnityEngine;

public class PlayerAttack : Attack
{
    [Tooltip("Secondary Attack Data")]
    public AttackData m_secondaryData;

    [Tooltip("Layers for attacks to ignore")]
    public LayerMask m_layerMask;

    protected Transform camTransform;

    private float currentDmg;

    private void Awake()
    {
        camTransform = Camera.main.transform;
        meleeCollider = GetComponent<BoxCollider>();
        if (meleeCollider)
        {
            Vector3 size = meleeCollider.size;
            meleeCollider.size = new Vector3(size.x, m_attackData.m_attackRange, size.z);
        }
    }

    public override void DoAttack() => DetermineAttack(m_attackData);

    public override void DoSecondary() => DetermineAttack(m_secondaryData);

    private void DetermineAttack(AttackData attackData)
    {
        /* determine coroutine to run based off attack type */
        if (attackData.m_attackType == AttackType.Melee) {
            currentDmg = attackData.m_damage;
            attackTimeoutDelta = attackData.m_attackTimeout;
            /* play start effects */
            if (attackData.m_startEffects.Length > 0)
            {
                foreach (GameObject effect in attackData.m_startEffects)
                {
                    Instantiate(effect, transform.position, transform.rotation, transform);
                }
            }
        } else if (attackData.m_attackType == AttackType.Ranged) {
            StartCoroutine(DelayedFire(attackData));
        }
        attackTimeoutDelta = m_attackData.m_attackTimeout; // start attack timeout
    }

    /* called fom player anim event */
    /* start collecting hits along a swing */
    public void CheckHit() => StartCoroutine(CollectHits());

    private IEnumerator CollectHits()
    {
        /* check all colliders hit over range of time to simulate a swing */
        float timer = 0f;
        while (timer < 0.5f)
        {
            Vector3 worldCenter = meleeCollider.transform.TransformPoint(meleeCollider.center);
            Vector3 worldHalfExtents = meleeCollider.transform.TransformVector(meleeCollider.size * 0.5f);
            Collider[] hits = Physics.OverlapBox(worldCenter, worldHalfExtents, meleeCollider.transform.rotation);
            foreach (Collider collider in hits)
            {
                var character = collider.gameObject.GetComponentInParent<Enemy>();
                if (character)
                {
                    Debug.Log(character.m_name + " took damage");
                    /* if we hit a character do hit effects and apply damage, then break */
                    CameraController.Instance.ShakeCamera(2.5f, 0.25f);
                    if (m_attackData.m_hitEffects.Length > 0)
                    {
                        foreach (GameObject effect in m_attackData.m_hitEffects)
                        {
                            Instantiate(effect, m_attackData.m_attackOrigin.position, m_attackData.m_attackOrigin.rotation, transform);
                        }
                    }
                    character.SendMessage("Damage", currentDmg);
                    yield break;
                }
            }
            yield return null;
            timer += Time.deltaTime;
        }
    }

    private IEnumerator DelayedFire(AttackData rangedData)
    {
        /* wait until we are at fire point to actually fire */
        yield return new WaitForSeconds(rangedData.m_fireDelay);
        GameObject bullet = Instantiate(rangedData.m_projPrefab, rangedData.m_attackOrigin.position, Quaternion.LookRotation(camTransform.forward));
        Projectile proj = bullet.GetComponent<Projectile>();
        proj.damage = rangedData.m_damage;
        if (Physics.Raycast(camTransform.position, camTransform.forward, out RaycastHit hit, Mathf.Infinity, ~m_layerMask)) {
            proj.target = hit.point;
            proj.hit = true;
        } else {
            proj.target = camTransform.position + camTransform.forward * rangedData.m_attackRange;
            proj.hit = false;
        }
    }
}