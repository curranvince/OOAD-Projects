using UnityEngine;

public class Shield : MonoBehaviour
{
    public bool blocking { get; set; }

    private void Start() => StopBlocking();

    public void Block() => blocking = true;

    public void StopBlocking() => blocking = false;
}