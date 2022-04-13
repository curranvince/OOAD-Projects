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

    /*
    [Header("Cinemachine")]
    [Tooltip("The follow target set in the Cinemachine Virtual Camera that the camera will follow")]
    public GameObject CinemachineCameraTarget;
    [Tooltip("How far in degrees can you move the camera up")]
    public float TopClamp = 70.0f;
    [Tooltip("How far in degrees can you move the camera down")]
    public float BottomClamp = -30.0f;
    [Tooltip("Additional degress to override the camera. Useful for fine tuning camera position when locked")]
    public float CameraAngleOverride = 0.0f;
    [Tooltip("For locking the camera position on all axis")]
    public bool LockCameraPosition = false;
    */

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

    // cinemachine
    // private float _cinemachineTargetYaw;
    // private float _cinemachineTargetPitch;

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

    // Update is called once per frame
    private void Update()
    {
        if (!MenuManager.Instance.onMainMenu)
        {
            if (!MenuManager.Instance.isPaused)
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
        if (!MenuManager.Instance.onMainMenu)
            CameraRotation();
    }

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

    // cache anim ids so we dont have to type strings on each access
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
    
    private void GroundedCheck()
    {
        // set sphere position, with offset
        Vector3 spherePosition = new Vector3(transform.position.x, transform.position.y - GroundedOffset, transform.position.z);
        Grounded = Physics.CheckSphere(spherePosition, GroundedRadius, GroundLayers, QueryTriggerInteraction.Ignore);
        animator.SetBool(_animIDGrounded, Grounded);
    }

    /*
    private void CameraRotation()
    {
        Vector2 input = lookAction.ReadValue<Vector2>();
        // if there is an input and camera position is not fixed
        if (input.sqrMagnitude >= _threshold && !LockCameraPosition)
        {
            _cinemachineTargetYaw += input.x * Time.deltaTime;
            _cinemachineTargetPitch += input.y * Time.deltaTime;
        }

        // clamp our rotations so our values are limited 360 degrees
        _cinemachineTargetYaw = ClampAngle(_cinemachineTargetYaw, float.MinValue, float.MaxValue);
        _cinemachineTargetPitch = ClampAngle(_cinemachineTargetPitch, BottomClamp, TopClamp);

        // Cinemachine will follow this target
        CinemachineCameraTarget.transform.rotation = Quaternion.Euler(_cinemachineTargetPitch + CameraAngleOverride, _cinemachineTargetYaw, 0.0f);
    }
    */

    private void CameraRotation()
    {
        Quaternion targetRotation = Quaternion.Euler(0, camTransform.eulerAngles.y, 0);
        transform.rotation = Quaternion.Lerp(transform.rotation, targetRotation, player.m_rotationSpeed * Time.deltaTime);
    }

    private void Move()
    {
        // set target speed based on move speed, sprint speed and if sprint is pressed
        float targetSpeed = player.m_walkSpeed; 
        
        Vector2 input = moveAction.ReadValue<Vector2>();

        // note: Vector2's == operator uses approximation so is not floating point error prone, and is cheaper than magnitude
        // if there is no input, set the target speed to 0
        if (input == Vector2.zero)
        {
            targetSpeed = 0.0f;
        }

        // move the player
        if (strafeAction.activeControl != null)
        {
            // handle strafing
            targetSpeed = player.m_strafeSpeed;
            _currentStrafeBlend = Vector2.SmoothDamp(_currentStrafeBlend, input, ref _animationVelocity, animationSmoothTime);
            Vector3 move = new Vector3(_currentStrafeBlend.x, 0, _currentStrafeBlend.y);
            move = move.x * camTransform.right.normalized + move.z * camTransform.forward.normalized;
            move.y = _verticalVelocity;
            controller.Move(move * Time.deltaTime * targetSpeed);
            animator.SetFloat("StrafeMoveX", _currentStrafeBlend.x);
            animator.SetFloat("StrafeMoveZ", _currentStrafeBlend.y);
            animator.SetBool(_animIDStrafe, true);
        }
        else
        {
            // handle walking/sprinting
            if (sprintAction.activeControl != null) targetSpeed = player.m_sprintSpeed; 
            // a reference to the players current horizontal velocity
            float currentHorizontalSpeed = new Vector3(controller.velocity.x, 0.0f, controller.velocity.z).magnitude;

            float speedOffset = 0.1f;

            // accelerate or decelerate to target speed
            if (currentHorizontalSpeed < targetSpeed - speedOffset || currentHorizontalSpeed > targetSpeed + speedOffset)
            {
                // creates curved result rather than a linear one giving a more organic speed change
                // note T in Lerp is clamped, so we don't need to clamp our speed
                _speed = Mathf.Lerp(currentHorizontalSpeed, targetSpeed, Time.deltaTime * player.m_accelerationRate);

                // round speed to 3 decimal places
                _speed = Mathf.Round(_speed * 1000f) / 1000f;
            }
            else
            {
                _speed = targetSpeed;
            }
            _animationBlend = Mathf.Lerp(_animationBlend, targetSpeed, Time.deltaTime * player.m_accelerationRate);
            
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
        if (pauseAction.WasPerformedThisFrame())
        {
            MenuManager.Instance.DeterminePause();
        }
    }

    private void JumpAndGravity()
    {
        if (Grounded)
        {
            // stop our velocity dropping infinitely when grounded
            if (_verticalVelocity < 0.0f)
            {
                _verticalVelocity = -2f;
            }

            // jump 
            if (_jumpTimeoutDelta <= 0.0f && strafeAction.activeControl == null && jumpAction.WasPerformedThisFrame())
            {
                _verticalVelocity = Mathf.Sqrt(player.m_jumpHeight * -2f * player.Gravity);

                // update animator if using character
                animator.SetBool(_animIDJump, true);
            }

            // jump timeout
            if (_jumpTimeoutDelta >= 0.0f)
            {
                _jumpTimeoutDelta -= Time.deltaTime;
            }
        }
        else
        {
            // reset the jump timeout timer
            _jumpTimeoutDelta = player.m_jumpTimeout;

            // fall timeout
            if (_fallTimeoutDelta >= 0.0f)
            {
                _fallTimeoutDelta -= Time.deltaTime;
            }
            else
            {
                // update animator if using character
                animator.SetBool(_animIDFreeFall, true);
            }
        }

        // apply gravity over time if under terminal (multiply by delta time twice to linearly speed up over time)
        if (_verticalVelocity < _terminalVelocity)
        {
            _verticalVelocity += player.Gravity * Time.deltaTime;
        }

        // need to use timed checks for attacks & heals since they can happen at any point
        if (player.attackObject && player.attackObject.attackTimeoutDelta <= (player.attackObject.m_attackData.m_attackTimeout-0.5f)) { 
            animator.SetBool(_animIDAttack, false);
            animator.SetBool(_animIDAttack2, false);
        }

        if (_healTimeoutDelta <= (player.m_healTimeout - 0.5f))
        {
            animator.SetBool(_animIDDrink, false);
        }

        _timeSinceRoll += Time.deltaTime;
    }

    private Vector3 InputToTarget(Vector2 input)
    {
        // normalise input direction
        Vector3 inputDirection = new Vector3(input.x, 0.0f, input.y).normalized;

        // note: Vector2's != operator uses approximation so is not floating point error prone, and is cheaper than magnitude
        // if there is a move input rotate player when the player is moving
        if (input != Vector2.zero)
        {
            _targetRotation = Mathf.Atan2(inputDirection.x, inputDirection.z) * Mathf.Rad2Deg + camTransform.eulerAngles.y;
            float rotation = Mathf.SmoothDampAngle(transform.eulerAngles.y, _targetRotation, ref _rotationVelocity, player.m_rotationSmoothTime);

            // rotate to face input direction relative to camera position
            transform.rotation = Quaternion.Euler(0.0f, rotation, 0.0f);
        }

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
        else
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

    /*
    private static float ClampAngle(float lfAngle, float lfMin, float lfMax)
    {
        if (lfAngle < -360f) lfAngle += 360f;
        if (lfAngle > 360f) lfAngle -= 360f;
        return Mathf.Clamp(lfAngle, lfMin, lfMax);
    }
    */
}
