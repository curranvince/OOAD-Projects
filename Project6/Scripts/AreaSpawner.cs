using System.Collections.Generic;
using UnityEngine;

public class AreaSpawner : MonoBehaviour
{
    [SerializeField]
    private GameObject[] m_enemyPrefabs;
    [SerializeField]
    [Tooltip("How often should enemies spawn")]
    private float m_spawnTimeout = 8f;
    [SerializeField]
    [Tooltip("How far away should the player be when enemies start spawning")]
    private float m_startSpawningRange = 10f;
    //[Tooltip("Min distance from spawner for enemies to spawn")]
    //private float m_spawnRangeMin = 1f;
    //[Tooltip("Max distance from spawner for enemies to spawn")]
    //private float m_spawnRangeMax = 5f;

    private GameObject _player;
    private float _spawnTimeoutDelta;
    private List<GameObject> _enemyObjects = new List<GameObject>();

    private void Start()
    {
        _player = GameObject.FindGameObjectWithTag("Player");
        _spawnTimeoutDelta = m_spawnTimeout;
    }

    private void Update()
    {
        if ((_player.transform.position - transform.position).magnitude < m_startSpawningRange)
        {
            if (_spawnTimeoutDelta <= 0)
            {
                _enemyObjects.Add(Instantiate(m_enemyPrefabs[Random.Range(0, m_enemyPrefabs.Length - 1)]));
                _spawnTimeoutDelta = m_spawnTimeout;
            }
            _spawnTimeoutDelta -= Time.deltaTime;
        }
    }

    public void Reset()
    {
       // foreach (GameObject enemy in _enemyObjects) Destroy(enemy);
    }
}