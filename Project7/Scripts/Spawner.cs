using UnityEngine;

public class Spawner : MonoBehaviour
{
    [SerializeField]
    private GameObject m_enemyPrefab;

    private GameObject _enemyObject;

    private void Start()
    {
        _enemyObject = Instantiate(m_enemyPrefab, transform.position, transform.rotation, transform);
    }

    public void Reset()
    {
        //DestroyImmediate(_enemyObject);
        _enemyObject = Instantiate(m_enemyPrefab, transform.position, transform.rotation, transform);
    }
}