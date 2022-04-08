using UnityEngine;
using UnityEngine.UI;

public class SettingsManager : MonoBehaviour
{
    public static SettingsManager Instance { get; private set; }

    public GameObject m_mainScreen;
    public GameObject m_pauseScreen;

    [SerializeField]
    private Toggle muteToggle;
    [SerializeField]
    private Slider volumeSlider;

    UserData currentData;

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
    }

    // set user settings on start
    // if none is found, JSONSave will create a default save
    private void Start()
    {
        UserData loadedData = JSONSave.Instance.LoadData("UserSettings") as UserData;
        currentData = loadedData;
        muteToggle.SetIsOnWithoutNotify(!currentData.m_volumeOn);
        UpdateSettings();
    }

    public void UpdateSettings()
    {
        // update audio
        AudioListener.pause = !currentData.m_volumeOn;
        AudioListener.volume = currentData.m_volumeLevel;
        // save new settings
        JSONSave.Instance.SaveData(currentData);
    }
    
    public void MuteToggle(bool isMuted)
    {
        currentData.m_volumeOn = !isMuted;
        UpdateSettings();
    }

    public void VolumeSliderChange()
    {
        currentData.m_volumeLevel = volumeSlider.value;
        UpdateSettings();
    }

    public void GoBack()
    {
        if (PauseManager.Instance.paused)
        {
            m_pauseScreen.SetActive(true);
        }
        else
        {
            m_mainScreen.SetActive(true);
        }
    }
}