using UnityEngine;
using Cinemachine;

public class PauseManager : MonoBehaviour
{
    public GameObject m_pauseScreen;

    [HideInInspector]
    public bool paused;

    public static PauseManager Instance { get; private set; }

    private void Awake()
    {
        if (Instance == null)
        {
            Instance = this;
        }
        else
        {
            Destroy(gameObject);
        }
        paused = false;
    }

    public void DeterminePause()
    {
        if (paused)
            Unpause();
        else
            Pause();
    }

    private void Pause()
    {
        Time.timeScale = 0;
        Cursor.lockState = CursorLockMode.None;
        //CinemachineInputProvider inputProvider = FindObjectOfType<CinemachineInputProvider>().GetComponent<CinemachineInputProvider>();
        //inputProvider.enabled = false;
        if (CameraController.Instance)
        {
            CameraController.Instance.SetInput(false);
            CameraController.Instance.SetReticle(false);
        }
        Player.Instance.SetHealthBar(false);
        m_pauseScreen.SetActive(true);
        paused = true;
    }

    private void Unpause()
    {
        Time.timeScale = 1;
        m_pauseScreen.SetActive(false);
        Cursor.lockState = CursorLockMode.Locked;
        //CinemachineInputProvider inputProvider = FindObjectOfType<CinemachineInputProvider>().GetComponent<CinemachineInputProvider>();
        //inputProvider.enabled = true;
        if (CameraController.Instance)
        {
            CameraController.Instance.SetInput(true);
            CameraController.Instance.SetReticle(true);
        }
        Player.Instance.SetHealthBar(true);
        paused = false;
    }
}