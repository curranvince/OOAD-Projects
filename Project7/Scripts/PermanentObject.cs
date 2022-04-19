using UnityEngine;

public class PermanentObject : MonoBehaviour
{
    private static PermanentObject Instance;

    private void Awake()
    {
        {
            DontDestroyOnLoad(gameObject);
            if (Instance == null)
            {
                Instance = this;
            }
            else
            {
                Destroy(gameObject);
            }
        }
    }
}