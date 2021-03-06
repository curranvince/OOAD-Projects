using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class Interactable : MonoBehaviour
{
    public float m_interactRange = 3f;
    [SerializeField]
    private LayerMask m_interactMask;

    [HideInInspector]
    protected string m_animClipName;

    [HideInInspector]
    public bool active;
    protected Player player;
    protected Camera mainCam;

    protected GameObject ui;

    protected virtual void Start()
    {
        active = false;
        player = GameObject.FindGameObjectWithTag("Player").GetComponent<Player>();
        mainCam = Camera.main;
        GameObject ref_ui = Instantiate(Resources.Load<GameObject>("InteractUI"), this.transform);
        ui = ref_ui;
        ui.SetActive(false);
    }

    protected virtual void Update()
    {
        if (!MenuManager.Instance.isPaused && PlayerInRange() && PlayerLookingAt())
        {
            Activate();
        } else
        {
            Deactivate();
        }
    }

    public bool Interact()
    {
        if (active)
        {
            if (m_animClipName != "")
            {
                player.PlayAnimation(m_animClipName);
            }
            DoInteraction();
            return true;
        }
        return false;
    }

    protected abstract void DoInteraction();

    protected bool PlayerInRange() => (player.transform.position - transform.position).magnitude < m_interactRange;

    protected bool PlayerLookingAt()
    {
        if (Physics.Raycast(mainCam.transform.position, mainCam.transform.forward, out RaycastHit hit, Mathf.Infinity, ~m_interactMask))
        {
            if (hit.collider.transform.name == transform.name)
            {
                return true;
            }
        }
        return false;
    }

    protected void Activate() {
        active = true;
        ui.SetActive(true);
    }

    protected void Deactivate() {
        active = false;
        ui.SetActive(false);
    }
}