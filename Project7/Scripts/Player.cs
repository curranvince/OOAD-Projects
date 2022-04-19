using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class Player : Character
{
    public static Player Instance;

    public float m_strafeSpeed = 2f;
    [Header("Rolling")]
    [Tooltip("Speed of roll")]
    public float m_rollSpeed;
    [Tooltip("Total time rolling. How long player does not have control")]
    public float m_rollTime;
    [Tooltip("Time invincible after roll starts")]
    public float m_rollInvincibilityTime = 0.5f;
    [Tooltip("Time between rolls, including time of first roll (should be higher than rollTime)")]
    public float m_rollTimeout = 1.5f;
    [Header("Jumping & Gravity")]
    [Tooltip("How high the player can jump")]
    public float m_jumpHeight = 1.2f;
    [Tooltip("Time required to pass before being able to jump again. Set to 0f to instantly jump again")]
    public float m_jumpTimeout = 0.50f;
    [Tooltip("Time required to pass before entering the fall state. Useful for walking down stairs")]
    public float m_fallTimeout = 0.15f;
    [Tooltip("Force of gravity (m/s)")]
    public float Gravity = -9.81f;

    [Header("Healing")]
    public bool m_invincible;
    public int m_healthPotions;
    public float m_healTimeout;
    public float m_healAmount;

    private Vector3 previousPosition;
    [HideInInspector]
    public Vector3 currentMovement;
    [HideInInspector]
    public bool triedToInteract;

    private Transform healthBar;
    private Image healthForeground;

    [HideInInspector]
    public PlayerData saveData = new PlayerData();

    [HideInInspector]
    public bool controls = true;

    private void Awake()
    {
        DontDestroyOnLoad(this);
        if (Instance == null)
        {
            Instance = this;
        }
        else
        {
            Destroy(gameObject);
        }
    }
    
    protected override void Start()
    {
        base.Start();
        triedToInteract = false;
        healthBar = transform.Find("UI/HealthBarUI");
        healthForeground = healthBar.transform.Find("Foreground").GetComponent<Image>();
    }

    void Update()
    {
        if (previousPosition != transform.position)
        {
            currentMovement = (transform.position - previousPosition);
            currentMovement /= Time.deltaTime;
            previousPosition = transform.position;
        }
    }

    public void SetHealthBar(bool newValue) => healthBar.gameObject.SetActive(newValue);

    public void PlayAnimation(string animation) => GetComponent<Animator>().Play("Interaction Layer." + animation, 3);

    public void StopMovement() => GetComponent<Animator>().SetFloat("MoveZ", 0f);

    public override void Damage(float amount)
    {
        if (!m_invincible)
        {
            Debug.Log("Player took damage");
            CameraController.Instance.ShakeCamera(3f, 0.2f);
            base.Damage(amount);
        }
    }

    protected override void UpdateHealthBar()
    {
        float width = 150 * (currentHealth / m_maxHealth);
        if (width < 0) width = 0;
        healthForeground.rectTransform.SetSizeWithCurrentAnchors(RectTransform.Axis.Horizontal, width);
    }

    protected override IEnumerator Die()
    {
        MenuManager.Instance.Pause(false);
        yield return StartCoroutine(MenuManager.Instance.FadeToBlack()); // wait for fade to finish
        MenuManager.Instance.ShowDeathScreen();
        /* teleport to last checkpoint & reset health */
        transform.position = saveData.spawnPosition;
        previousPosition = transform.position;
        currentHealth = m_maxHealth; 
        UpdateHealthBar();
        /* reset enemies & spawners */
        Enemy[] enemies = FindObjectsOfType<Enemy>();
        foreach (Enemy enemy in enemies) { Destroy(enemy.gameObject); }
        Spawner[] spawners = FindObjectsOfType<Spawner>();
        foreach (Spawner spawner in spawners) { spawner.Reset(); }
    }

    public void Heal(float amount)
    {
        currentHealth = Mathf.Clamp(currentHealth + amount, 0, m_maxHealth);
        m_healthPotions -= 1;
        UpdateHealthBar();
    }

    public void Hit()
    {
        PlayerAttack pattackObject = (PlayerAttack)attackObject;
        pattackObject.CheckHit();
    }

    public void SetFromData(PlayerData playerData)
    {
        /* copy data */
        saveData.spawnPosition = playerData.spawnPosition;
        saveData.playerClass = playerData.playerClass;
        saveData.fileName = playerData.fileName;
        /* set position */
        transform.position = saveData.spawnPosition;
        previousPosition = transform.position;
        /* set class*/
        m_attacks.Clear();
        if (saveData.playerClass == "Mage")
        {
            m_attacks.Add(Resources.Load<GameObject>("MagicStaff"));
        } 
        else if (saveData.playerClass == "Melee")
        {
            m_attacks.Add(Resources.Load<GameObject>("SharpSword"));
        }
        attackObject = m_attacks[0].GetComponent<Attack>();
        m_shield = Resources.Load<GameObject>("PlayerShield");
        EquipWeapons();
    }

    /* destroy the players attack object so it doesnt 'hang around' */
    public void ClearWeapons()
    {
        if (attackObject)
            Destroy(attackObject.gameObject);
    }
}