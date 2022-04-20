using System.IO;
using UnityEngine;

public class SaveManager : MonoBehaviour
{
    public static SaveManager Instance { get; private set; }

    private string path = "";

    private void Awake() => Instance = this;

    /* add in directory path & suffix so we only deal with fileNames */
    private void SetPath(string fileName) => path = Application.persistentDataPath + Path.AltDirectorySeparatorChar + fileName + ".json";

    /* convert data to JSON and write to a file */
    public void SaveData(JSONData data)
    {
        SetPath(data.fileName);
        Debug.Log("Saving data to " + path);

        string json = JsonUtility.ToJson(data);
        Debug.Log(json);

        using StreamWriter writer = new StreamWriter(path);
        writer.Write(json);
    }

    /* load data from a specified file and convert to proper data type */ 
    public JSONData LoadData(string fileName)
    {
        string json = "";
        SetPath(fileName);

        /* try to read file with given name */
        try {
            using StreamReader reader = new StreamReader(path);
            json = reader.ReadToEnd();
        } catch (System.IO.FileNotFoundException) {
            /* if theres no user settings create a default profile */
            if (fileName == "UserSettings")
                return new UserData() { fileName = fileName };
        }
        
        /* return data as proper type by checking the filename */
        if (fileName == "UserSettings") {
            return JsonUtility.FromJson<UserData>(json);
        } else {
            return JsonUtility.FromJson<PlayerData>(json);
        }
    }
}