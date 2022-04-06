using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;

public abstract class PlayerAttack : Attack
{ 
    [Tooltip("Secondary Attack Data")]
    public AttackData m_secondaryData;
    
    [Tooltip("Layers for attacks to ignore")]
    public LayerMask m_layerMask;

    protected Transform camTransform;
    
    protected virtual void Awake()
    {
        camTransform = Camera.main.transform;
    }
    
    public virtual void CheckHit() { }
}
