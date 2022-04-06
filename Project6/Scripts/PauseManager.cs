using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Cinemachine;
using UnityEngine.InputSystem;

public class PauseManager : MonoBehaviour
{
    [SerializeField]
    private GameObject menu;

    [HideInInspector]
    public bool paused;

    public static PauseManager Instance { get; private set; }

    private void Awake()
    {
        Instance = this;
        menu = transform.Find("Menu").gameObject;
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
        CinemachineInputProvider inputProvider = GameObject.FindObjectOfType<CinemachineInputProvider>().GetComponent<CinemachineInputProvider>();
        inputProvider.enabled = false;
        AudioListener.pause = true;
        menu.SetActive(true);
        paused = true;
    }

    private void Unpause()
    {
        Time.timeScale = 1;
        menu.SetActive(false);
        Cursor.lockState = CursorLockMode.Locked;
        CinemachineInputProvider inputProvider = GameObject.FindObjectOfType<CinemachineInputProvider>().GetComponent<CinemachineInputProvider>();
        inputProvider.enabled = true;
        AudioListener.pause = false;
        paused = false;
    }
}