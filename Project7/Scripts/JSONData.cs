using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;


[Serializable]
public abstract class JSONData
{
    public string fileName;
}

public class PlayerData : JSONData
{
    public string playerClass;
    public Vector3 spawnPosition;
    public Vector3 spawnRotation;
}

public class UserData : JSONData
{
    public bool volumeOn = true;
    [Range(0f,1f)]
    public float volumeLevel = 1f;
}