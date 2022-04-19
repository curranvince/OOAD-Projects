using UnityEngine;
using UnityEngine.UI;

public class UIHealthBar : MonoBehaviour
{
    [HideInInspector]
    public Transform target { get; set; }
    private Image foregroundImage;
    private Image backgroundImage;

    private Camera mainCam;

    private void Awake()
    {
        mainCam = Camera.main;
        foregroundImage = gameObject.transform.Find("Foreground").GetComponent<Image>();
        backgroundImage = gameObject.transform.Find("Background").GetComponent<Image>();
    }

    void LateUpdate()
    {
        /* make sure health bar doesnt appear when loking in opposite direction */
        Vector3 direction = (target.position - mainCam.transform.position).normalized;
        bool isBehind = Vector3.Dot(direction, mainCam.transform.forward) <= 0.0f;
        foregroundImage.enabled = !isBehind;
        backgroundImage.enabled = !isBehind;
        /* set health bar position */
        transform.position = target.position; //mainCam.WorldToScreenPoint(target.position);
    }

    public void SetHealthbar(float percentage)
    {
        /* fill foreground image with percent equal to health */
        float parentWidth = GetComponent<RectTransform>().rect.width;
        float width = parentWidth * percentage;
        foregroundImage.rectTransform.SetSizeWithCurrentAnchors(RectTransform.Axis.Horizontal, width);
    }
}
