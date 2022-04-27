using System.Collections;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class Boss : Enemy
{
    private GameObject bossBar;
    private Image healthForeground;
    private TMP_Text bossLabel;

    public void SetHealthBar(bool value)
    {
        if (!bossBar) CreateHealthBar();
        bossBar.SetActive(value);
    }

    protected override void Start()
    {
        base.Start();
        CreateHealthBar();
        SetHealthBar(false);
    }

    private void CreateHealthBar()
    {
        bossBar = Instantiate(Resources.Load<GameObject>("BossUI"), gameObject.transform);    // add a boss health bar
        healthForeground = bossBar.transform.Find("HealthBarUI/Foreground").GetComponent<Image>();
        bossLabel = bossBar.transform.Find("HealthBarUI/BossName").GetComponent<TMP_Text>();
        bossLabel.text = m_name;
    }

    public override void UpdateHealthBar()
    {
        float width = 150 * (currentHealth / m_maxHealth);
        if (width < 0) width = 0;
        healthForeground.rectTransform.SetSizeWithCurrentAnchors(RectTransform.Axis.Horizontal, width);
    }

    protected override IEnumerator Die()
    {
        SetHealthBar(false);
        if (controller)
            controller.stateMachine.ChangeState(EnemyStateID.Death);
        Destroy(gameObject);
        /* alert menu manager that game has been won */
        MenuManager.Instance.ShowWinScreen();
        yield return null;
    }
}