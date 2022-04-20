using UnityEngine;
using Cinemachine;

public class CameraController : MonoBehaviour
{
    public static CameraController Instance { get; private set; }
    
    [SerializeField]
    private GameObject m_reticle;
    
    private CinemachineVirtualCamera vCam;
    private CinemachineBasicMultiChannelPerlin perlin;
    private CinemachineInputProvider inputProvider;
    private NoiseSettings calmNoise;
    private NoiseSettings shakeNoise;
    private float shakeTimerDelta;

    public void SetRotation(Vector3 eulerAngles) => vCam.transform.eulerAngles = eulerAngles;

    public void SetInput(bool newValue) => inputProvider.enabled = newValue;

    public void SetReticle(bool newValue) => m_reticle.SetActive(newValue);

    private void Awake()
    {
        /* ensure only one CameraController is ever made */
        DontDestroyOnLoad(this);
        if (Instance == null)
            Instance = this;
        else
            Destroy(gameObject);
    }

    private void Start()
    {
        /* get references to componenets */
        vCam = GetComponent<CinemachineVirtualCamera>();
        perlin = vCam.GetCinemachineComponent<CinemachineBasicMultiChannelPerlin>();
        inputProvider = GetComponent<CinemachineInputProvider>();
        /* load noise (shake) settings */
        calmNoise = Resources.Load("CalmShake") as NoiseSettings;
        shakeNoise = Resources.Load("BigShake") as NoiseSettings;
        /* set initial values */
        shakeTimerDelta = 0;
        perlin.m_NoiseProfile = calmNoise;
        perlin.m_AmplitudeGain = 0.5f;
        perlin.m_FrequencyGain = 0.5f;
    }

    private void Update()
    {
        if (shakeTimerDelta > 0)
        {
            shakeTimerDelta -= Time.deltaTime;
            /* if no longer need to shake, go back to normal noise settings */
            if (perlin.m_AmplitudeGain != 0.5f && shakeTimerDelta <= 0)
            {
                perlin.m_NoiseProfile = calmNoise;
                perlin.m_AmplitudeGain = 0.5f;
                perlin.m_FrequencyGain = 0.5f;
            }
        }
    }

    public void ShakeCamera(float intensity, float time)
    {
        /* start camera shaking and set shake timer */
        perlin.m_NoiseProfile = shakeNoise;
        perlin.m_AmplitudeGain = intensity;
        perlin.m_FrequencyGain = 5f;
        shakeTimerDelta = time;
    }
}