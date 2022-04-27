using UnityEngine;

public abstract class Interactable : MonoBehaviour
{
    [Tooltip("How far away can the player interact with the object")]
    public float m_interactRange = 3f;
    [SerializeField]
    private LayerMask m_interactMask; // what layers can the interaction happen through

    public GameObject[] m_startEffects;

    [HideInInspector]
    protected string m_animClipName; // what animation to play when player interacts with the object

    [HideInInspector]
    public bool active;
    protected Player player;
    protected Camera mainCam;

    protected GameObject ui;

    protected abstract void DoInteraction();

    /* see if player is within the interaction range */
    protected bool PlayerInRange() => (player.transform.position - transform.position).magnitude < m_interactRange;

    /* see if player is looking at 'this' */
    protected bool PlayerLookingAt() => (Physics.Raycast(mainCam.transform.position, mainCam.transform.forward, out RaycastHit hit, Mathf.Infinity, ~m_interactMask) && hit.collider.transform.name == transform.name);

    protected virtual void Start()
    {
        active = false;
        player = GameObject.FindGameObjectWithTag("Player").GetComponent<Player>();
        mainCam = Camera.main;
        GameObject ref_ui = Instantiate(Resources.Load<GameObject>("InteractUI"), this.transform);
        ui = ref_ui;
        ui.SetActive(false);
    }

    /* activate if not on a menu, player in range, and player looking at, else deactivate */
    protected virtual void Update()
    {
        if ((!MenuManager.Instance || !MenuManager.Instance.isPaused) && PlayerInRange() && PlayerLookingAt())
            Set(true);
        else
            Set(false);
    }

    /* on Interact, play anim clip and call DoInteraction */
    /* return wether interaction was started */
    public bool Interact()
    {
        if (active)
        {
            /* play animation & start effects, and do the interaction */
            if (m_animClipName != "") player.PlayAnimation(m_animClipName);
            if (m_startEffects.Length > 0)
            {
                foreach (var effect in m_startEffects)
                    GameObject.Instantiate(effect, transform.position, transform.rotation);
            }
            DoInteraction();
        }
        return active;
    }

    /* set wether the interactable object is active */
    protected void Set(bool setAs)
    {
        active = setAs;
        ui.SetActive(setAs);
    }
}