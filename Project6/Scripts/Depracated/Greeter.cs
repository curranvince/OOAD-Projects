using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Greeter : Interactable
{
    protected override void DoInteraction()
    {
        Debug.Log("Hi i got interacted with!");
    }
}
