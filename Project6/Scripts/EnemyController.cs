using UnityEngine;
using UnityEngine.AI;

public class EnemyController : Controller
{
    [Header("AI")]
    [Tooltip("Distance enemy can see")]
    public float m_alertDistance = 20.0f;
    [Tooltip("Distance enemy exits combat from")]
    public float m_followDistance = 40.0f;
    [Tooltip("What the enemy can see through")]
    public LayerMask m_sightLayer;
    [SerializeField]
    [Tooltip("Initial state of the AI")]
    private EnemyStateID initialState;
    
    // [HideInInspector]
    public EnemyStateID currentState;

    [HideInInspector]
    public Enemy character;
    [HideInInspector]
    public NavMeshAgent agent;
    [HideInInspector]
    public EnemyStateMachine stateMachine;
    [HideInInspector]
    public Transform playerTransform;

    protected override void Awake()
    {
        base.Awake();
        agent = GetComponent<NavMeshAgent>();
        character = GetComponent<Enemy>();
        
        stateMachine = new EnemyStateMachine(this);
        stateMachine.RegisterState(new IdleState());
        stateMachine.RegisterState(new CombatState());
        stateMachine.RegisterState(new DeathState());
        stateMachine.ChangeState(initialState);

        if (playerTransform == null)
        {
            playerTransform = GameObject.FindGameObjectWithTag("Player").transform;
        }
    }

    private void Update()
    {
        stateMachine.Update();
        currentState = stateMachine.currentState;
    }
}