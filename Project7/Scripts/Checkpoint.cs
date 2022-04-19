public class Checkpoint : Interactable
{
    protected override void Start()
    {
        base.Start();
        m_animClipName = "TouchCheckpoint";
    }

    /* heal the player, set their spawn position, and save the game */
    protected override void DoInteraction()
    {
        player.Heal(player.m_maxHealth);
        player.saveData.spawnPosition = this.transform.position;
        SaveManager.Instance.SaveData(player.saveData);
    }
}