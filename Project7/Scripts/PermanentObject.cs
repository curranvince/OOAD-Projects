using UnityEngine;

public class PermanentObject : MonoBehaviour
{
    private static PermanentObject Instance;

    private void Awake()
    {
        /* ensure only one perm object, and that it lives forever */
        DontDestroyOnLoad(gameObject);
        if (Instance == null) Instance = this;
        else Destroy(gameObject);
    }
}