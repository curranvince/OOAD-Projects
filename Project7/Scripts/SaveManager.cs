using System.IO;
using UnityEngine;

public class SaveManager : MonoBehaviour
{
    public static SaveManager Instance { get; private set; }

    private string path = "";

    private void Awake() => Instance = this;

    private void SetPath(string fileName) => path = Application.persistentDataPath + Path.AltDirectorySeparatorChar + fileName + ".json";

    public void SaveData(JSONData data)
    {
        SetPath(data.fileName);
        Debug.Log("Saving data to " + path);

        string json = JsonUtility.ToJson(data);
        Debug.Log(json);

        using StreamWriter writer = new StreamWriter(path);
        writer.Write(json);
    }

    public JSONData LoadData(string fileName)
    {
        string json = "";
        SetPath(fileName);

        try
        {
            using StreamReader reader = new StreamReader(path);
            json = reader.ReadToEnd();
            //Debug.Log("Read json as: " + json);
        }
        catch (System.IO.FileNotFoundException)
        {
            // if theres no user settings create a default profile
            if (fileName == "UserSettings")
                return new UserData()
                {
                    fileName = fileName
                };
        }
        
        if (fileName == "UserSettings")
        {
            UserData data = JsonUtility.FromJson<UserData>(json);
            return data;
        } 
        else
        {
            PlayerData data = JsonUtility.FromJson<PlayerData>(json);
            return data;
        }
    }
}