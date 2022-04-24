using System.Collections;
using UnityEngine;
using UnityEngine.InputSystem;
using UnityEngine.Animations.Rigging;
using Cinemachine;

public abstract class Controller : MonoBehaviour
{
    [HideInInspector]
    public Animator animator;

    protected virtual void Awake()
    {
        animator = GetComponent<Animator>();
    }
}

[RequireComponent(typeof(CharacterController), typeof(PlayerInput))]
public class PlayerController : Controller
{ 
    [Header("Player Grounded")]
    [Tooltip("If the character is grounded or not. Not part of the CharacterController built in grounded check")]
    public bool Grounded = true;
    [Tooltip("Useful for rough ground")]
    public float GroundedOffset = -0.14f;
    [Tooltip("The radius of the grounded check. Should match the radius of the CharacterController")]
    public float GroundedRadius = 0.28f;
    [Tooltip("What layers the character uses as ground")]
    public LayerMask GroundLayers;

    [SerializeField]
    private float animationSmoothTime = 0.1f;
    
    [Header("Aim Animation")]
    [SerializeField]
    private Transform aimTarget;
    [SerializeField]
    private float aimDistance = 10f;

    [Header("Interact")]
    [SerializeField]
    private LayerMask interactMask;

    private CharacterController controller;
    private PlayerInput playerInput;
    private Transform camTransform;
    private RigBuilder rigBuilder;
    private Player player;

    // input actions
    private InputAction moveAction;
    // private InputAction lookAction;
    private InputAction sprintAction;
    private InputAction strafeAction;
    private InputAction jumpAction;
    private InputAction attackAction;
    private InputAction attackAction2;
    private InputAction blockAction; 
    private InputAction interactAction;
    private InputAction drinkAction;
    private InputAction pauseAction;

    // movement
    private bool _rolling;
    private float _speed;
    private float _animationBlend;
    private float _targetRotation = 0.0f;
    private float _rotationVelocity;
    private float _verticalVelocity;
    private float _terminalVelocity = 53.0f;
    private Vector2 _currentStrafeBlend;
    private Vector2 _animationVelocity;

    // timeout deltatime
    private float _jumpTimeoutDelta;
    private float _fallTimeoutDelta;
    private float _healTimeoutDelta;
    private float _timeSinceRoll;

    // animation IDs
    //private int _animIDMoveX;
    private int _animIDMoveZ;
    private int _animIDGrounded;
    private int _animIDJump;
    private int _animIDFreeFall;
    private int _animIDRoll;
    private int _animIDStrafe;
    private int _animIDBlock;
    private int _animIDAttack;
    private int _animIDAttack2;
    private int _animIDInteract;
    private int _animIDDrink;

    protected override void Awake()
    {
        base.Awake();
        controller = GetComponent<CharacterController>();
        rigBuilder = GetComponent<RigBuilder>();
        playerInput = GetComponent<PlayerInput>();
        player = GetComponent<Player>();
        camTransform = Camera.main.transform;
        aimTarget = transform.Find("PGeometry/AimRig/ref_AimTarget");
        AssignActions();
        AssignAnimationIDs();
        Cursor.lockState = CursorLockMode.Locked;
    }

    private void OnEnable()
    {
        attackAction.performed += _ => Attack();
        attackAction2.performed += _ => Attack2();
        drinkAction.performed += _ => DrinkPotion();
    }

    private void OnDisable()
    {
        attackAction.performed -= _ => Attack();
        attackAction2.performed -= _ => Attack2(); 
        drinkAction.performed -= _ => DrinkPotion();
    }

    private void Update()
    {
        if (!MenuManager.Instance || !MenuManager.Instance.onMainMenu)      // do nothing while on main menu
        {
            if (!MenuManager.Instance || !MenuManager.Instance.isPaused)    // only check for pause button while on pause menu
            {
                if (player.controls)
                {
                    UpdateAimTarget();
                    GroundedCheck();
                    RollCheck();
                    JumpAndGravity();
                    if (!_rolling)
                    {
                        BlockCheck();
                        Move();
                    }
                }
                CheckInteractions();
            }
            CheckForPause();
        }   
    }

    private void LateUpdate()
    {
        if (!camTransform)
            camTransform = Camera.main.transform;
        if (!MenuManager.Instance || !MenuManager.Instance.onMainMenu)
            RotateToCamera();
    }

    /* cache input actions for easy access */ 
    private void AssignActions()
    {
        moveAction = playerInput.actions["Move"];
        // lookAction = playerInput.actions["Look"];
        sprintAction = playerInput.actions["Sprint"];
        strafeAction = playerInput.actions["Strafe"];
        jumpAction = playerInput.actions["Jump"];
        attackAction = playerInput.actions["Attack"];
        attackAction2 = playerInput.actions["Attack2"];
        blockAction = playerInput.actions["Block"];
        interactAction = playerInput.actions["Interact"];
        drinkAction = playerInput.actions["Drink"];
        pauseAction = playerInput.actions["Pause"];
    }

    /* cache anim ids so we dont have to type strings on each access */
    private void AssignAnimationIDs()
    {
        //_animIDMoveX = Animator.StringToHash("MoveX");
        _animIDMoveZ = Animator.StringToHash("MoveZ");
        _animIDGrounded = Animator.StringToHash("Grounded");
        _animIDJump = Animator.StringToHash("Jump");
        _animIDFreeFall = Animator.StringToHash("FreeFall");
        _animIDStrafe = Animator.StringToHash("Strafing");
        _animIDRoll = Animator.StringToHash("Rolling");
        _animIDBlock = Animator.StringToHash("Blocking");
        _animIDAttack = Animator.StringToHash("Attack");
        _animIDAttack2 = Animator.StringToHash("Attack2");
        _animIDInteract = Animator.StringToHash("Interact");
        _animIDDrink = Animator.StringToHash("Drink");
    }
    
    /* check if player is touching ground using small offset */
    private void GroundedCheck()
    {
        Vector3 spherePosition = new Vector3(transform.position.x, transform.position.y - GroundedOffset, transform.position.z);
        Grounded = Physics.CheckSphere(spherePosition, GroundedRadius, GroundLayers, QueryTriggerInteraction.Ignore);
        animator.SetBool(_animIDGrounded, Grounded);
    }
    
    /* keep player looking in same direction as camera */
    private void RotateToCamera()
    {
        Quaternion targetRotation = Quaternion.Euler(0, camTransform.eulerAngles.y, 0);
        transform.rotation = Quaternion.Lerp(transform.rotation, targetRotation, player.m_rotationSpeed * Time.deltaTime);
    }

    /* handle player movement */
    private void Move()
    {
        /* read user input and set base speed if there is any */ 
        Vector2 input = moveAction.ReadValue<Vector2>();
        float targetSpeed = (input == Vector2.zero) ? 0.0f : player.m_walkSpeed;

        /* use different blends for strafe and walk/run so theres more abrupt change when you begin crouching */
        if (strafeAction.activeControl != null) {
            /* handle strafing */
            targetSpeed = player.m_strafeSpeed;
            _currentStrafeBlend = Vector2.SmoothDamp(_currentStrafeBlend, input, ref _animationVelocity, animationSmoothTime);
            Vector3 move = new Vector3(_currentStrafeBlend.x, 0, _currentStrafeBlend.y);
            move = move.x * camTransform.right.normalized + move.z * camTransform.forward.normalized;
            move.y = _verticalVelocity;
            controller.Move(move * Time.deltaTime * targetSpeed);
            animator.SetFloat("StrafeMoveX", _currentStrafeBlend.x);
            animator.SetFloat("StrafeMoveZ", _currentStrafeBlend.y);
            animator.SetBool(_animIDStrafe, true);
        } else {
            /* handle walking/sprinting */
            /* set speed to sprint if necessary and cache some variables */
            if (sprintAction.activeControl != null) targetSpeed = player.m_sprintSpeed;                               
            float currentHorizontalSpeed = new Vector3(controller.velocity.x, 0.0f, controller.velocity.z).magnitude; 
            float speedOffset = 0.1f;

            /* accelerate or decelerate to target speed */
            if (currentHorizontalSpeed < targetSpeed - speedOffset || currentHorizontalSpeed > targetSpeed + speedOffset)
            {
                /* creates curved result rather than a linear one giving a more organic speed change */
                _speed = Mathf.Lerp(currentHorizontalSpeed, targetSpeed, Time.deltaTime * player.m_accelerationRate);        // note lerp has clamp built in
                _speed = Mathf.Round(_speed * 1000f) / 1000f;                                                                // round to 3 decimals
            } else {
                _speed = targetSpeed;                   /* if we dont need to accel/decel then keep speed the same */
            }

            /* get final speed blend */
            _animationBlend = Mathf.Lerp(_animationBlend, targetSpeed, Time.deltaTime * player.m_accelerationRate);

            /* convert input to direction and combine with speed to move the character */
            Vector3 targetDirection = InputToTarget(input);
            animator.SetBool(_animIDStrafe, false);
            controller.Move(targetDirection.normalized * (_speed * Time.deltaTime) + new Vector3(0.0f, _verticalVelocity, 0.0f) * Time.deltaTime);
            animator.SetFloat(_animIDMoveZ, _animationBlend);
        }
    }

    /* check if player interacted with anything */
    /* if interaction button was pressed, shoot a ray from camera */
    /* if it collides with an "Interactable" object, Interact with the Interactable component of it */
    private void CheckInteractions()
    {
        if (interactAction.WasPerformedThisFrame())
        {
            if (Physics.Raycast(camTransform.position, camTransform.forward, out RaycastHit hit, Mathf.Infinity, ~interactMask))
            {
                if (hit.collider.CompareTag("Interactable"))
                {
                    hit.collider.gameObject.GetComponent<Interactable>().Interact();
                }
            }
        }
    }

    /* see if player tried to pause game this frame */
    private void CheckForPause()
    {
        if (pauseAction.WasPerformedThisFrame()) MenuManager.Instance.DeterminePause();
    }

    /* apply gravity and check for player jumping */
    private void JumpAndGravity()
    {
        if (Grounded)
        {
            /* stop our velocity dropping infinitely when grounded */
            if (_verticalVelocity < 0.0f) _verticalVelocity = -2f;

            /* if player jumps, add to their vertical velocity and alert animator */
            if (_jumpTimeoutDelta <= 0.0f && strafeAction.activeControl == null && jumpAction.WasPerformedThisFrame())
            {
                _verticalVelocity = Mathf.Sqrt(player.m_jumpHeight * -2f * player.Gravity);
                animator.SetBool(_animIDJump, true);                                        
            }

            /* jump timeout */
            if (_jumpTimeoutDelta >= 0.0f) _jumpTimeoutDelta -= Time.deltaTime;
        }
        else
        {
            /* reset the jump timeout timer if in air */
            _jumpTimeoutDelta = player.m_jumpTimeout;

            /* iterate fall timer or alert animator if actually falling */
            if (_fallTimeoutDelta >= 0.0f) _fallTimeoutDelta -= Time.deltaTime;
            else animator.SetBool(_animIDFreeFall, true);
        }

        // apply gravity over time if under terminal (multiply by delta time twice to linearly speed up over time)
        if (_verticalVelocity < _terminalVelocity) _verticalVelocity += player.Gravity * Time.deltaTime;

        /* need to use timed checks for attacks & heals since they can happen at any point */
        if (player.attackObject && player.attackObject.attackTimeoutDelta <= (player.attackObject.m_attackData.m_attackTimeout-0.5f)) { 
            animator.SetBool(_animIDAttack, false);
            animator.SetBool(_animIDAttack2, false);
        }

        if (_healTimeoutDelta >= 0.0f) _healTimeoutDelta -= Time.deltaTime;
        if (_healTimeoutDelta <= (player.m_healTimeout - 0.4f)) animator.SetBool(_animIDDrink, false);

        /* iterate roll timer */
        _timeSinceRoll += Time.deltaTime;
    }

    /* convert given input to a direction */
    private Vector3 InputToTarget(Vector2 input)
    {
        /* normalise input direction */
        Vector3 inputDirection = new Vector3(input.x, 0.0f, input.y).normalized;

        /* if there is input rotate to face same direction as camera */
        if (input != Vector2.zero)
        {
            _targetRotation = Mathf.Atan2(inputDirection.x, inputDirection.z) * Mathf.Rad2Deg + camTransform.eulerAngles.y;
            float rotation = Mathf.SmoothDampAngle(transform.eulerAngles.y, _targetRotation, ref _rotationVelocity, player.m_rotationSmoothTime);

            transform.rotation = Quaternion.Euler(0.0f, rotation, 0.0f);
        }

        /* return target rotation (in eulers) */
        return Quaternion.Euler(0.0f, _targetRotation, 0.0f) * Vector3.forward;
    }

    private void RollCheck()
    {
        player.triedToInteract = false;
        animator.SetBool(_animIDInteract, false);

        if (Grounded)
        {
            // reset the fall timeout timer
            _fallTimeoutDelta = player.m_fallTimeout;

            // update animator 
            animator.SetBool(_animIDRoll, false);
            animator.SetBool(_animIDJump, false);
            animator.SetBool(_animIDFreeFall, false);
            if (_timeSinceRoll >= player.m_rollInvincibilityTime)
            {
                player.m_invincible = false;
            }
            if (_timeSinceRoll >= 1.15f)
            {
                rigBuilder.enabled = true;
            }
        }

        if (_timeSinceRoll >= player.m_rollTimeout && jumpAction.WasPerformedThisFrame() && strafeAction.activeControl != null)
        {
            Vector2 input = moveAction.ReadValue<Vector2>();
            if (input.magnitude > 0)
            {
                player.m_invincible = true;
                StartCoroutine(Roll(input));
            }
        }
    }

    IEnumerator Roll(Vector2 input)
    {
        rigBuilder.enabled = false;
        _rolling = true;
        animator.SetBool(_animIDRoll, true);
        _timeSinceRoll = 0f;
        float timer = 0;
        Vector3 targetDirection = InputToTarget(input);
        while (timer < player.m_rollTime)
        {
            controller.Move(targetDirection.normalized * (player.m_rollSpeed * Time.deltaTime) + new Vector3(0.0f, _verticalVelocity, 0.0f) * Time.deltaTime);
            timer += Time.deltaTime;
            yield return null;
        }
        _rolling = false;
    }

    private void UpdateAimTarget()
    {
        if (camTransform)
            aimTarget.position = camTransform.position + camTransform.forward * aimDistance;
    }

    private void BlockCheck()
    {
        if (player.shieldObject && blockAction.activeControl != null)
        {
            animator.SetBool(_animIDBlock, true);
            player.shieldObject.Block();
        }
        else if (player.shieldObject)
        {
            animator.SetBool(_animIDBlock, false);
            player.shieldObject.StopBlocking();
        }
    }

    private void Attack()
    {
        if (player.controls && !MenuManager.Instance.isPaused && !MenuManager.Instance.onMainMenu && player.attackObject.CanAttack())
        {
            player.attackObject.SendMessage("DoAttack"); // uses polymorphism, whereas calling directly would not
            animator.SetBool(_animIDAttack, true); 
        }
    }

    private void Attack2()
    {
        if (player.controls && !MenuManager.Instance.isPaused && !MenuManager.Instance.onMainMenu && player.attackObject.CanAttack())
        {
            player.attackObject.SendMessage("DoSecondary");
            animator.SetBool(_animIDAttack2, true);
        }
    }

    private void DrinkPotion()
    {
        if (player.controls && !MenuManager.Instance.isPaused && !MenuManager.Instance.onMainMenu && player.m_healthPotions > 0 && _healTimeoutDelta <= 0)
        {
            animator.SetBool(_animIDDrink, true);
            _healTimeoutDelta = player.m_healTimeout;
            player.Heal(player.m_healAmount);
        }
    }
}
