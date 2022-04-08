using System.Collections;
using UnityEngine;

public class Enemy : Character
{
    [HideInInspector]
    public UIHealthBar healthBar;
    private EnemyController controller;

    protected override void Start()
    {
        base.Start();
        EquipWeapons();
        controller = GetComponent<EnemyController>();
        /* if enemy has health bar slot, give them a health bar */
        if (transform.Find("HealthBarSlot"))
        {
            GameObject ref_bar = Instantiate(Resources.Load<GameObject>("HealthBarUI"), gameObject.transform.Find("HealthBarSlot"));
            healthBar = ref_bar.GetComponentInChildren<UIHealthBar>();
            healthBar.target = gameObject.transform.Find("HealthBarSlot");
        }
    }

    public override void Damage(float amount)
    {
        base.Damage(amount);
        if (controller && controller.stateMachine.currentState == EnemyStateID.Idle) 
            controller.stateMachine.ChangeState(EnemyStateID.Combat);
    }

    protected override void UpdateHealthBar()
    {
        if (healthBar) healthBar.SetHealthbar(currentHealth / m_maxHealth);
    }

    protected override IEnumerator Die()
    {
        if (controller)
            controller.stateMachine.ChangeState(EnemyStateID.Death);
        Destroy(gameObject);
        yield return null;
    }
}