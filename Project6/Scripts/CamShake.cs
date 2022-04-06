using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Cinemachine;

public class CamShake : MonoBehaviour
{
    public static CamShake Instance { get; private set; }

    private CinemachineVirtualCamera vCam;
    private CinemachineBasicMultiChannelPerlin perlin;
    private NoiseSettings calmNoise;
    private NoiseSettings shakeNoise;
    private float shakeTimerDelta;
    //private float shakeTimeout;
    //private float startingIntensity;
    
    private void Start()
    {
        Instance = this;
        vCam = GetComponent<CinemachineVirtualCamera>();
        perlin = vCam.GetCinemachineComponent<CinemachineBasicMultiChannelPerlin>();
        calmNoise = Resources.Load("CalmShake") as NoiseSettings;
        shakeNoise = Resources.Load("BigShake") as NoiseSettings;
        shakeTimerDelta = 0;
        perlin.m_NoiseProfile = calmNoise;
        perlin.m_AmplitudeGain = 0.5f;
        perlin.m_FrequencyGain = 0.5f;
    }

    public void ShakeCamera(float intensity, float time)
    {
        perlin.m_NoiseProfile = shakeNoise;
        perlin.m_AmplitudeGain = intensity;
        perlin.m_FrequencyGain = 5f;
        //startingIntensity = intensity;
        shakeTimerDelta = time;
        //shakeTimeout = time;
    }

    private void Update()
    {
        if (shakeTimerDelta > 0)
        {
            shakeTimerDelta -= Time.deltaTime;
            //perlin.m_AmplitudeGain = Mathf.Lerp(startingIntensity, 0, (1 - (shakeTimerDelta / shakeTimeout)));
            if (perlin.m_AmplitudeGain != 0.5f && shakeTimerDelta <= 0)
            {
                perlin.m_NoiseProfile = calmNoise;
                perlin.m_AmplitudeGain = 0.5f;
                perlin.m_FrequencyGain = 0.5f;
            }
        }
    }
}
