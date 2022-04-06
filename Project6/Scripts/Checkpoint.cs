using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Checkpoint : Interactable
{
    protected override void DoInteraction()
    {
        player.checkpoint = this;
    }
}