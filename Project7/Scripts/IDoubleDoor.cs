using UnityEngine;

public class IDoubleDoor : Interactable
{

    [SerializeField]
    private float m_speed = 1f;
    [SerializeField]
    private float m_rotationAmount = 110f;
    public float m_forwardDirection = 0;

    private GameObject leftDoor;
    private GameObject rightDoor;
    
    private Door ld;
    private Door rd;

    protected override void Start()
    {
        base.Start();
        m_animClipName = "OpenDoor";
        leftDoor = transform.Find("LeftDoor").gameObject;
        rightDoor = transform.Find("RightDoor").gameObject;
        ld = leftDoor.AddComponent<Door>();
        ld.m_left = true;
        ld.m_speed = m_speed;
        ld.m_rotationAmount = m_rotationAmount;
        ld.m_forwardDirection = m_forwardDirection;
        rd = rightDoor.AddComponent<Door>();
        rd.m_left = false;
        rd.m_speed = m_speed;
        rd.m_rotationAmount = m_rotationAmount;
        rd.m_forwardDirection = m_forwardDirection;
    }

    protected override void DoInteraction()
    {
        if (!ld.isOpen)
        {
            ld.Open();
            rd.Open();
        } else
        {
            ld.Close();
            rd.Close();
        }
    }
}
