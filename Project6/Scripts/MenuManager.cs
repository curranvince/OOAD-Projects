using System.Collections;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MenuManager : MonoBehaviour
{
    public static MenuManager Instance { get; private set; }

    [Tooltip("Name of the scene to load when the game starts")]
    public string m_gameScene;

    [Header("Screens")]
    public GameObject m_mainScreen;
    public GameObject m_pauseScreen;
    public GameObject m_deathScreen;
    public GameObject m_blackScreen;

    [Header("Main Menu")]
    public GameObject[] m_hideOnStart;
    public GameObject m_mainForeground;

    [Header("Options Menu")]
    [SerializeField]
    private Toggle m_muteToggle;
    [SerializeField]
    private Slider m_volumeSlider;

    public bool isPaused { get; set; }
    public bool onMainMenu { get; private set; }
    public string classChoice { get; private set; }

    private UserData currentUserData;

    /* ensure the same MenuManager is available throughout life of application */
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

    private void Start()
    {
        /* load & set user settings */
        currentUserData = SaveManager.Instance.LoadData("UserSettings") as UserData;
        /* set options screen UI appropiately */
        m_muteToggle.SetIsOnWithoutNotify(!currentUserData.m_volumeOn);
        m_volumeSlider.value = currentUserData.m_volumeLevel;
        UpdateSettings();
        onMainMenu = true;
        isPaused = false;
    }

    /* quit the game */
    public void Quit() => Application.Quit();

    /* called when a player chooses their class */
    public void SetClassChoice(string classInput) => classChoice = classInput;

    public void ShowDeathScreen() => m_deathScreen.SetActive(true);

    /* start game with new save file */
    public void StartNewGame(string fileInput)
    {
        Debug.Log(fileInput);
        PlayerData newPlayerData = new PlayerData
        {
            fileName = fileInput,
            spawnPosition = Vector3.zero,
            playerClass = classChoice
        };
        Debug.Log("Created new save game: " + newPlayerData.fileName);
        StartCoroutine(StartGame(newPlayerData));
    }

    /* start game from loaded data */
    public void StartGameFromLoad(string fileName)
    {
        if (SaveManager.Instance.LoadData(fileName) is PlayerData data)
        {
            m_mainForeground.SetActive(false);
            StartCoroutine(StartGame(data));
        }
        else
        {
            m_mainForeground.SetActive(true);
            Debug.Log("Could not find save game: " + fileName);
        }
    }

    /* start the game with given save data*/
    private IEnumerator StartGame(PlayerData playerData)
    {
        yield return StartCoroutine(FadeToBlack(1f));            // wait for screen to go black
        foreach (GameObject toHide in m_hideOnStart) toHide.SetActive(false);  // hide some menu UI
        AsyncOperation asyncLoadLevel = SceneManager.LoadSceneAsync(m_gameScene, LoadSceneMode.Single);     // load the game scene
        while (!asyncLoadLevel.isDone) yield return null;                      // wait for level to load
        yield return new WaitForSecondsRealtime(1f);                           // wait a second so awakes can finish
        /* if player has no spawn position set it as the world spawn */
        if (playerData.spawnPosition == Vector3.zero) playerData.spawnPosition = GameObject.FindGameObjectWithTag("Spawn").GetComponent<Checkpoint>().gameObject.transform.position;
        /* save and set player data */
        SaveManager.Instance.SaveData(playerData);
        Player player = GameObject.FindGameObjectWithTag("Player").GetComponent<Player>();
        player.SetFromData(playerData);
        StartCoroutine(UnfadeFromBlack(0.5f));                     // unfade from black
        /* turn game UI on */
        CameraController.Instance.SetReticle(true);
        Player.Instance.SetHealthBar(true);
        onMainMenu = false;
    }

    /* update all the games settings to match current user data */
    public void UpdateSettings()
    {
        /* update audio */
        AudioListener.pause = !currentUserData.m_volumeOn;
        AudioListener.volume = currentUserData.m_volumeLevel;
        /* save the settings we just applied */
        SaveManager.Instance.SaveData(currentUserData);
    }

    /* handle mute button being toggled */
    public void MuteToggle(bool isMuted)
    {
        currentUserData.m_volumeOn = !isMuted;
        UpdateSettings();
    }

    /* handle volume slider being changed */
    public void VolumeSliderChange()
    {
        currentUserData.m_volumeLevel = m_volumeSlider.value;
        UpdateSettings();
    }

    /* close the settings menu */
    public void ExitSettings()
    {
        if (isPaused)
        {
            m_pauseScreen.SetActive(true);
        }
        else
        {
            m_mainScreen.SetActive(true);
        }
    }

    /* toggle pausing */
    public void DeterminePause()
    {
        if (isPaused)
            Resume();
        else
            Pause();
    }

    /* pause the game */
    public void Pause(bool showPauseScreen = true)
    {
        /* stop time, unlock cursor, swap game UI for pause screen */
        Time.timeScale = 0;
        Cursor.lockState = CursorLockMode.None;
        if (CameraController.Instance)
        {
            CameraController.Instance.SetInput(false);
            CameraController.Instance.SetReticle(false);
        }
        Player.Instance.SetHealthBar(false);
        if (showPauseScreen) m_pauseScreen.SetActive(true);
        isPaused = true;
    }

    /* resume game */
    public void Resume(bool clearPauseScreen = true)
    {
        if (clearPauseScreen) m_pauseScreen.SetActive(false);
        Cursor.lockState = CursorLockMode.Locked;
        if (CameraController.Instance)
        {
            CameraController.Instance.SetInput(true);
            CameraController.Instance.SetReticle(true);
        }
        Player.Instance.SetHealthBar(true);
        Player.Instance.controls = true;
        Time.timeScale = 1;                                         // resume time 
        isPaused = false;
    }

    /* return to main menu from pause menu */
    public void ReturnToMainMenu()
    {
        if (isPaused)
        {
            /* unpause game, hide game UI, & reset Player */
            Resume();
            CameraController.Instance.SetReticle(false);
            Player.Instance.SetHealthBar(false);
            Player.Instance.ClearWeapons();
            /* show main menu and unlock cursor */
            m_mainForeground.SetActive(true);
            SceneManager.LoadScene("MainMenu", LoadSceneMode.Single);
            Cursor.lockState = CursorLockMode.None;
            onMainMenu = true;
        }
    }

    public IEnumerator FadeToBlack(float fadeSpeed = 2f)
    {
        Color color = m_blackScreen.GetComponent<Image>().color;
        float fadeAmount;

        while (m_blackScreen.GetComponent<Image>().color.a < 1)
        {
            fadeAmount = color.a + (fadeSpeed * Time.unscaledDeltaTime);

            color = new Color(color.r, color.g, color.b, fadeAmount);
            m_blackScreen.GetComponent<Image>().color = color;
            yield return null;
        }
    }

    public IEnumerator UnfadeFromBlack(float fadeSpeed = 2f)
    {
        Color color = m_blackScreen.GetComponent<Image>().color;
        float fadeAmount;

        while (m_blackScreen.GetComponent<Image>().color.a > 0)
        {
            fadeAmount = color.a - (fadeSpeed * Time.unscaledDeltaTime);

            color = new Color(color.r, color.g, color.b, fadeAmount);
            m_blackScreen.GetComponent<Image>().color = color;
            yield return null;
        }
    }

    public void ExitDeathScreen()
    {
        StartCoroutine(UnfadeFromBlack(0.25f));
        Resume(false);
    }
}