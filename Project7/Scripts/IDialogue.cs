using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class IDialogue : Interactable
{
    [SerializeField]
    private float m_talkingSpeed = 50f;

    [SerializeField]
    private ScriptableDialogue m_dialogue;

    private Animator animator;
    private GameObject dialogueContainer;
    private GameObject dialogueBox;
    private TMP_Text textLabel;

    private bool talking;

    private Coroutine writingCo;

    private int _animIDTalking;
    private int _animIDTalking2;

    private bool allDone;
    private bool continueDialogue;

    protected override void Start()
    {
        base.Start();
        m_animClipName = "";
        animator = GetComponent<Animator>();
        _animIDTalking = Animator.StringToHash("Talking");
        _animIDTalking2 = Animator.StringToHash("Talking2");
        dialogueBox = GameObject.FindWithTag("DialogueBox");
        dialogueContainer = dialogueBox.transform.Find("DialogueContainer").gameObject;
        textLabel = dialogueContainer.transform.Find("DialogueText").GetComponent<TMP_Text>();
    }

    protected override void Update()
    {
        if (!talking && !MenuManager.Instance.isPaused && PlayerInRange() && PlayerLookingAt())
        {
            Activate();
        } 
        else if ((!talking && (!PlayerInRange() || !PlayerLookingAt())) || MenuManager.Instance.isPaused)
        {
            Deactivate();
        }

        if (MenuManager.Instance.isPaused)
        {
            dialogueContainer.SetActive(false);
            if (talking) EndInteraction();
        }
    }

    protected override void DoInteraction()
    {
        if (!talking)
        {
            StartTalking();
        }
        else if (allDone)
        {
            EndInteraction();
        }
        else
        {
            continueDialogue = true;
        }
    }

    private void StartTalking()
    {
        talking = true;
        ui.SetActive(false);
        dialogueContainer.SetActive(true);
        player.controls = false;
        CameraController.Instance.SetInput(false);
        player.StopMovement();
        animator.SetBool(_animIDTalking, true);
        ShowDialogue();
    }

    private void ShowDialogue() => StartCoroutine(StepThroughDialog(m_dialogue));

    private IEnumerator StepThroughDialog(ScriptableDialogue scriptableDialogue)
    {
        allDone = false;
        
        Speech last = scriptableDialogue.Dialogue[scriptableDialogue.Dialogue.Length-1];
        foreach (Speech speech in scriptableDialogue.Dialogue)
        {
            continueDialogue = false;

            animator.SetBool(_animIDTalking2, speech.important);

            yield return WriteText(speech.dialogue);

            if (!speech.Equals(last))
            {
                yield return new WaitUntil(() => continueDialogue);
            }
        }

        allDone = true;
    }

    private void EndInteraction()
    {
        // end dialogue interaction
        talking = false;
        dialogueContainer.SetActive(false);
        textLabel.text = string.Empty;  // ensure text label is empty
        animator.SetBool(_animIDTalking, false);
        animator.SetBool(_animIDTalking2, false);
        if (writingCo != null)
            StopCoroutine(writingCo);
        player.controls = true;
        CameraController.Instance.SetInput(true);
    }

    private IEnumerator WriteText(string textToType)
    {
        textLabel.text = string.Empty;  // ensure text label is empty

        float t = 0;                    // track time since coroutine started
        int charIndex = 0;              // index of character that should be printed up to by now

        /* while we have more chars to type, increment time & index, write, then wait */
        while (charIndex < textToType.Length)
        {
            t += Time.deltaTime * m_talkingSpeed;
            charIndex = Mathf.FloorToInt(t);
            charIndex = Mathf.Clamp(charIndex, 0, textToType.Length);

            textLabel.text = textToType.Substring(0, charIndex);

            yield return null;
        }

        textLabel.text = textToType;    // ensure full text is printed
    }
}