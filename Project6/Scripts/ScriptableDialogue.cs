using System;
using UnityEngine;

[Serializable]
public class Speech
{
    [TextArea]
    public string dialogue;
    public bool important;

    public override bool Equals(object obj)
    {
        if (!(obj is Speech other)) return false;

        return (this.dialogue.Equals(other.dialogue));
    }

    public override int GetHashCode() => base.GetHashCode();
}

[CreateAssetMenu(menuName = "Dialogue/ScriptableDialogue")]
public class ScriptableDialogue : ScriptableObject
{
    [SerializeField]
    private Speech[] dialogue;

    public Speech[] Dialogue => dialogue;
}