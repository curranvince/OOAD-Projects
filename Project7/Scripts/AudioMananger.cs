using System.Collections;
using UnityEngine;

public class AudioMananger : MonoBehaviour
{
    public static AudioMananger Instance;

    [SerializeField]
    private GameObject[] m_ambientSounds;
    [SerializeField]
    [Range(0.0f, 1.0f)]
    private float m_ambientVolume;

    private bool _play { get; set; }         // play the music
    private bool _toggleChange { get; set; } // detect when music is toggled, ensures music isn’t played multiple times
    private AudioSource _audioSource { get; set; }
    private GameObject _refObject;

    private void Awake()
    {
        if (Instance == null) Instance = this;
        else Destroy(gameObject);
    }

    private void Start()
    {
        _refObject = Instantiate(m_ambientSounds[0], gameObject.transform);
        _audioSource = _refObject.GetComponent<AudioSource>();
        _play = true;  // ensure the toggle is set to true for the music to play at start-up
        _toggleChange = true;
    }

    /* check if we should be playing music, & if its just been toggled or not */
    /* Adapted from: https://docs.unity3d.com/ScriptReference/AudioSource.html */
    private void Update()
    {
        //Check to see if you just set the toggle to positive
        if (_play == true && _toggleChange == true)
        {
            _audioSource.Play();
            _toggleChange = false;
        }
        //Check if you just set the toggle to false
        if (_play == false && _toggleChange == true)
        {
            _audioSource.Stop();
            _toggleChange = false;
        }
    }

    public void ToggleAmbientLoop() => _toggleChange = true; 

    /* wait for old music to fade out, then change music and fade in new sound */ 
    public IEnumerator ChangeAmbientLoop(int index)
    {
        yield return StartCoroutine(FadeOut(1f));
        Destroy(_refObject);
        _refObject = Instantiate(m_ambientSounds[index], gameObject.transform);
        _audioSource = _refObject.GetComponent<AudioSource>();
        StartCoroutine(FadeIn(1f));
    }

    /* lower volume each frame until it reaches 0 */
    /* Adapted from: https://forum.unity.com/threads/fade-out-audio-source.335031/ */
    private IEnumerator FadeOut(float fadeTime)
    {
        float startVolume = _audioSource.volume;

        while (_audioSource.volume > 0.0f)
        {
            _audioSource.volume -= startVolume * Time.deltaTime / fadeTime;

            yield return null;
        }
    }

    /* raise volume each frame until it reaches 1 */
    private IEnumerator FadeIn(float fadeTime)
    {
        float startVolume = _audioSource.volume;

        while (_audioSource.volume < m_ambientVolume)
        {
            _audioSource.volume += startVolume * Time.deltaTime / fadeTime;

            yield return null;
        }
    }
}