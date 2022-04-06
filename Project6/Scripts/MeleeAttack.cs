using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MeleeAttack : PlayerAttack
{
    private float currentDmg;
    private BoxCollider meleeCollider;

    protected override void Awake()
    {
        base.Awake();
        meleeCollider = GetComponent<BoxCollider>();
        Vector3 size = meleeCollider.size;
        meleeCollider.size = new Vector3(size.x, m_attackData.m_attackRange, size.z);
    }

    protected override void Start()
    {
        base.Start();
    }

    protected override void Update()
    {
        base.Update();
    }

    /* update dmg, timer, do start effects */
    public override void DoAttack()
    {
        currentDmg = m_attackData.m_damage;
        attackTimeoutDelta = m_attackData.m_attackTimeout;
        if (m_attackData.m_startEffects.Length > 0)
        {
            foreach (GameObject effect in m_attackData.m_startEffects)
            {
                Instantiate(effect, transform.position, transform.rotation, transform);
            }
        }
    }

    /* update dmg, timer, do start effects */
    public override void DoSecondary()
    {
        currentDmg = m_secondaryData.m_damage;
        attackTimeoutDelta = m_secondaryData.m_attackTimeout;
        if (m_secondaryData.m_startEffects.Length > 0)
        {
            foreach (GameObject effect in m_secondaryData.m_startEffects)
            {
                Instantiate(effect, transform.position, transform.rotation, transform);
            }
        }
    }

    /* called fom player anim event */
    /* start collecting hits along a swing */
    public override void CheckHit()
    {
        StartCoroutine(CollectHits());
    }

    IEnumerator CollectHits()
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
                    CamShake.Instance.ShakeCamera(2.5f, 0.25f);
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
}