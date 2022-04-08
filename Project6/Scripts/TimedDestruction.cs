using UnityEngine;

public class TimedDestruction : MonoBehaviour
{
    [SerializeField]
    private float m_timeToDestroy;

    void Start()
    {
        Destroy(gameObject, m_timeToDestroy);
    }
}
