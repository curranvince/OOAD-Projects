using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class FadeToBlack : MonoBehaviour
{
    public GameObject blackSquare;

    public static FadeToBlack Instance { get; private set; }

    private void Awake()
    {
        Instance = this;
        blackSquare.SetActive(true);
        StartCoroutine(Unfade());
    }

    public IEnumerator Fade(float fadeSpeed = 2f)
    {
        Color color = blackSquare.GetComponent<Image>().color;
        float fadeAmount;

        while (blackSquare.GetComponent<Image>().color.a < 1)
        {
            fadeAmount = color.a + (fadeSpeed * Time.unscaledDeltaTime);

            color = new Color(color.r, color.g, color.b, fadeAmount);
            blackSquare.GetComponent<Image>().color = color;
            yield return null;
        }
    }

    public IEnumerator Unfade(float fadeSpeed = 2f)
    {
        Color color = blackSquare.GetComponent<Image>().color;
        float fadeAmount;

        while (blackSquare.GetComponent<Image>().color.a > 0)
        {
            fadeAmount = color.a - (fadeSpeed * Time.unscaledDeltaTime);

            color = new Color(color.r, color.g, color.b, fadeAmount);
            blackSquare.GetComponent<Image>().color = color;
            yield return null;
        }
    }
}