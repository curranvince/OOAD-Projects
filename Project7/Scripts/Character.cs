using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class Character : MonoBehaviour
{
    [Header("Character")]
    public string m_name;
    public float m_maxHealth = 100f;
    [Range(0.0f, 2.0f)]
    public float m_damageModifier = 1.0f;

    [Header("Movement")]
    public float m_walkSpeed = 2.5f;
    public float m_sprintSpeed = 5.5f;
    public float m_rotationSpeed = 5f;
    public float m_accelerationRate = 10.0f;
    [Range(0.0f, 0.3f)]
    public float m_rotationSmoothTime = 0.12f; // how long for char to face movement direction

    [Header("Combat")]
    public List<GameObject> m_attacks = new List<GameObject>(); 
    //public GameObject[] m_attacks;
    public Transform m_attackParent;
    public GameObject m_shield;
    public Transform m_shieldParent;

    [Header("Effects")]
    public GameObject[] m_deathEffects;

    [HideInInspector]
    public float currentHealth;

    [HideInInspector]
    public Attack attackObject;
    [HideInInspector]
    public Shield shieldObject;

    protected virtual void Start()
    {
        currentHealth = m_maxHealth;
    }

    protected abstract IEnumerator Die();
    protected virtual void UpdateHealthBar() { }

    public virtual void Damage(float amount)
    {
        float dmgTaken = amount * m_damageModifier;
        currentHealth -= dmgTaken;
        UpdateHealthBar();
        if (currentHealth <= 0.0f) { StartCoroutine(Die()); }
    }

    public void SwitchToWeapon(int index)
    {
        if (m_attacks.Count >= (index - 1))
        {
            Destroy(attackObject.gameObject);
            GameObject newattack = Instantiate(m_attacks[index], m_attackParent) as GameObject;
            attackObject = newattack.GetComponent<Attack>();
        }
    }

    protected void EquipWeapons()
    {
        //if (attackObject) Destroy(attackObject);
        // bool set = false;
        Controller controller = GetComponent<Controller>();

        if (m_attacks.Count > 0)
        {
            GameObject newattack = Instantiate(m_attacks[0], m_attackParent) as GameObject;
            attackObject = newattack.GetComponent<Attack>();
            controller.animator.runtimeAnimatorController = attackObject.m_animator;
            // set = true;
        }
        if (m_shield)
        {
            GameObject newshield = Instantiate(m_shield, m_shieldParent) as GameObject;
            shieldObject = newshield.GetComponent<Shield>();
            // if (!set) controller.animator.runtimeAnimatorController = shieldObject.m_animator;
        }
    }
}