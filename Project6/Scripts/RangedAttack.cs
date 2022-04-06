using System;
using System.Collections;
using UnityEngine;

public class RangedAttack : PlayerAttack
{
    protected override void Start()
    {
        base.Start();
    }

    public override void DoAttack()
    {
        if (attackTimeoutDelta <= 0)
        {
            StartCoroutine(DelayedFire(m_attackData));
            attackTimeoutDelta = m_attackData.m_attackTimeout;
        }
    }

    public override void DoSecondary()
    {
        if (attackTimeoutDelta <= 0)
        {
            StartCoroutine(DelayedFire(m_secondaryData));
            attackTimeoutDelta = m_secondaryData.m_attackTimeout;
        }
    }

    IEnumerator DelayedFire(AttackData rangedData)
    {
        yield return new WaitForSeconds(rangedData.m_fireDelay);
        RaycastHit hit;
        GameObject bullet = GameObject.Instantiate(rangedData.m_projPrefab, rangedData.m_attackOrigin.position, Quaternion.LookRotation(camTransform.forward));
        Projectile proj = bullet.GetComponent<Projectile>();
        proj.damage = rangedData.m_damage;
        if (Physics.Raycast(camTransform.position, camTransform.forward, out hit, Mathf.Infinity, ~m_layerMask))
        {
            proj.target = hit.point;
            proj.hit = true;
        }
        else
        {
            proj.target = camTransform.position + camTransform.forward * rangedData.m_attackRange;
            proj.hit = false;
        }
    }
}