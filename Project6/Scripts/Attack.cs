using System;
using UnityEngine;

public abstract class Attack : MonoBehaviour
{
    [Serializable]
    public class AttackData
    {
        public Transform m_attackOrigin;
        public float m_damage = 10f;
        public float m_attackRange = 25f;
        public float m_attackTimeout = 1.2f;
        public float m_dodgeWindow = 0.45f;

        [Header("Ranged Options")]
        public GameObject m_projPrefab;
        public float m_fireDelay = 0.55f;

        [Header("Effects")]
        public GameObject[] m_startEffects;
        public GameObject[] m_hitEffects;
    }

    [Tooltip("Animator to override with")]
    public RuntimeAnimatorController m_animator;
    [Tooltip("Main Attack Data")]
    public AttackData m_attackData;
    
    [HideInInspector]
    public float attackTimeoutDelta { get; set; }

    protected virtual void Start()
    {
        attackTimeoutDelta = 0;
    }

    protected virtual void Update()
    {
        if (attackTimeoutDelta >= 0) attackTimeoutDelta -= Time.deltaTime;
    }

    public bool CanAttack() { return attackTimeoutDelta <= 0; }

    public abstract void DoAttack();
    public abstract void DoSecondary();
}
