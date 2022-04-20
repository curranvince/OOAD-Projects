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

    protected abstract void DoInteraction();

    protected bool PlayerInRange() => (player.transform.position - transform.position).magnitude < m_interactRange;

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
            if (m_animClipName != "") player.PlayAnimation(m_animClipName);
            DoInteraction();
        }
        return active;
    }

    protected void Set(bool setAs)
    {
        active = setAs;
        ui.SetActive(setAs);
    }
}