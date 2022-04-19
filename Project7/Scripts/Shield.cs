using UnityEngine;

public class Shield : MonoBehaviour
{
    public bool blocking { get; set; }

    private void Start()
    {
        blocking = false;
    }

    public void Block()
    {
        blocking = true;
    }

    public void StopBlocking()
    {
        blocking = false;
    }
}
