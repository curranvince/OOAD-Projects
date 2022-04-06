using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Door : MonoBehaviour
{
    public bool m_left;
    public float m_speed;
    public float m_rotationAmount;
    public float m_forwardDirection;

    private Player player;
    private Vector3 _startRotation;
    private Vector3 _forward;
    
    private Coroutine animCoroutine;

    public bool isOpen { get; private set; }

    private void Start()
    {
        player = GameObject.FindGameObjectWithTag("Player").GetComponent<Player>();
        _startRotation = transform.rotation.eulerAngles;
        _forward = -transform.forward;
        isOpen = false;
    }

    public void Open()
    {
        if (animCoroutine != null)
        {
            StopCoroutine(animCoroutine);
        }

        float dot = Vector3.Dot(_forward, (player.transform.position - transform.position).normalized);
        animCoroutine = StartCoroutine(RotateOpen(dot));
    }

    private IEnumerator RotateOpen(float forwardAmount)
    {
        Quaternion startRotation = transform.rotation;
        Quaternion endRotation;
        if (m_left)
        {
            endRotation = (forwardAmount >= m_forwardDirection) ? Quaternion.Euler(new Vector3(0, _startRotation.y - m_rotationAmount, 0)) : Quaternion.Euler(new Vector3(0, _startRotation.y + m_rotationAmount, 0));
        } else
        {
            endRotation = (forwardAmount <= m_forwardDirection) ? Quaternion.Euler(new Vector3(0, _startRotation.y - m_rotationAmount, 0)) : Quaternion.Euler(new Vector3(0, _startRotation.y + m_rotationAmount, 0));
        }
        
        isOpen = true;

        float time = 0;
        while (time < 1)
        {
            transform.rotation = Quaternion.Slerp(startRotation, endRotation, time);
            yield return null;
            time += Time.deltaTime * m_speed;
        }
    }

    public void Close()
    {
        if (animCoroutine != null)
        {
            StopCoroutine(animCoroutine);
        }

        animCoroutine = StartCoroutine(RotateClosed());
    }

    private IEnumerator RotateClosed()
    {
        Quaternion startRotation = transform.rotation;
        Quaternion endRotation = Quaternion.Euler(_startRotation);

        isOpen = false;

        float time = 0;
        while (time < 1)
        {
            transform.rotation = Quaternion.Slerp(startRotation, endRotation, time);
            yield return null;
            time += Time.deltaTime * m_speed;
        }
    }
}
