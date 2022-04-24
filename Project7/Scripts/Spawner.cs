using UnityEngine;

public class Spawner : MonoBehaviour
{
    [SerializeField]
    private GameObject m_enemyPrefab;

    private void Start() => Reset();

    public void Reset() => Instantiate(m_enemyPrefab, transform.position, transform.rotation, transform);
}