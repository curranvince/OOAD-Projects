using System.Collections;
using UnityEngine;
using UnityEngine.SceneManagement;

public class MainMenuManager : MonoBehaviour
{
    public static MainMenuManager Instance { get; private set; }

    public GameObject[] hideOnStart;
    public GameObject mainForeground;

    public bool onMenu { get; private set; }
    public string classChoice { get; set; }

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
        onMenu = true;
    }

    public void SetClassChoice(string classInput) 
    {
        classChoice = classInput;
    }

    public void StartNewGame(string fileInput)
    {
        Debug.Log(fileInput);
        PlayerData newPlayerData = new PlayerData
        {
            fileName = fileInput,
            spawnPosition = Vector3.zero,
            playerClass = classChoice
        };
        //Debug.Log("new file name: " + newPlayerData.fileName);
        StartCoroutine(StartGame(newPlayerData));
    }

    public void StartGameFromLoad(string fileName)
    {
        PlayerData data = JSONSave.Instance.LoadData(fileName) as PlayerData;
        if (data != null)
        {
            mainForeground.SetActive(false);
            StartCoroutine(StartGame(data));
        } 
        else
        {   
            mainForeground.SetActive(true);
            Debug.Log("Could not load game " + fileName);
        }
    }

    private IEnumerator StartGame(PlayerData playerData)
    {
        // wait for screen to go black
        yield return StartCoroutine(FadeToBlack.Instance.Fade(1f));
        foreach (GameObject toHide in hideOnStart) toHide.SetActive(false); // hide some menu UI
        // load the game scene
        AsyncOperation asyncLoadLevel = SceneManager.LoadSceneAsync("SimpleTest", LoadSceneMode.Single);
        // wait until the level finishes loading
        while (!asyncLoadLevel.isDone)
        {
            yield return null;
        }
        // wait a second so awake/starts can run
        yield return new WaitForSecondsRealtime(1f);
        // set spawn position to world spwan, if player save has no spawn point yet
        Debug.Log("setting player data");
        if (playerData.spawnPosition == Vector3.zero)
        {
            playerData.spawnPosition = GameObject.FindGameObjectWithTag("Spawn").GetComponent<Checkpoint>().gameObject.transform.position;
        }
        // save and set player data
        JSONSave.Instance.SaveData(playerData);
        Player player = GameObject.FindGameObjectWithTag("Player").GetComponent<Player>();
        player.SetFromData(playerData);
        // turn game UI on
        CameraController.Instance.SetReticle(true);
        Player.Instance.SetHealthBar(true);
        StartCoroutine(FadeToBlack.Instance.Unfade(0.5f));
        onMenu = false;
    }

    public void ReturnToMainMenu()
    {
        /* unpause game and hide game UI */
        PauseManager.Instance.DeterminePause();
        CameraController.Instance.SetReticle(false);
        Player.Instance.SetHealthBar(false);
        Player.Instance.ClearWeapons();
        /* show main menu and unlock cursor */
        mainForeground.SetActive(true); 
        SceneManager.LoadScene("MainMenu", LoadSceneMode.Single);
        Cursor.lockState = CursorLockMode.None;
        onMenu = true;
    }

    public void Quit()
    {
        Application.Quit();
    }
}