using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public abstract class Interactable : MonoBehaviour
{
    public float interactRange;
    [SerializeField]
    private LayerMask interactMask;

    [HideInInspector]
    public bool active;
    protected Player player;
    protected Camera mainCam;

    private GameObject ui;

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
        if (PlayerInRange() && PlayerLookingAt())
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
            DoInteraction();
            return true;
        }
        return false;
    }

    protected abstract void DoInteraction();

    private bool PlayerInRange()
    {
        return (player.transform.position - transform.position).magnitude < interactRange;
    }

    private bool PlayerLookingAt()
    {
        RaycastHit hit;
        if (Physics.Raycast(mainCam.transform.position, mainCam.transform.forward, out hit, Mathf.Infinity, ~interactMask))
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

