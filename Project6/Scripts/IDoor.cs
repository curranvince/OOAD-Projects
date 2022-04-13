using UnityEngine;

public class IDoor : Interactable
{
    public bool m_left;
    [SerializeField]
    private float m_speed = 1f;
    [SerializeField]
    private float m_rotationAmount = 110f;
    public float m_forwardDirection = 0;

    private Door door;

    protected override void Start()
    {
        base.Start();
        m_animClipName = "OpenDoor";
        door = gameObject.AddComponent<Door>();
        door.m_speed = m_speed;
        door.m_rotationAmount = m_rotationAmount;
        door.m_forwardDirection = m_forwardDirection;
        door.m_left = m_left;
    }

    protected override void DoInteraction()
    {
        if (!door.isOpen)
        {
            door.Open();
        }
        else
        {
            door.Close();
        }
    }
}
