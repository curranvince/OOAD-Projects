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
            Set(true);
        else if ((!talking && (!PlayerInRange() || !PlayerLookingAt())) || MenuManager.Instance.isPaused)
            Set(false);

        /* if player pauses during interaction, just end it */
        if (MenuManager.Instance.isPaused)
        {
            dialogueContainer.SetActive(false);
            if (talking) EndInteraction();
        }
    }

    /* start, continue, or end interaction based off current state */
    protected override void DoInteraction()
    {
        if (!talking)
            StartTalking();
        else if (allDone)
            EndInteraction();
        else
            continueDialogue = true;
    }

    /* begin a dialogue interaction */
    private void StartTalking()
    {
        talking = true;
        ui.SetActive(false);                        // remove normal game ui
        dialogueContainer.SetActive(true);          // turn on dialogue container
        player.controls = false;                    // turn off player controls
        CameraController.Instance.SetInput(false);   
        player.StopMovement();                      // ensure player stops moving (so walking anim does not continue) 
        animator.SetBool(_animIDTalking, true);     // set NPC talking anim to true
        ShowDialogue();                             // begin the dialogue interaction
    }

    private void ShowDialogue() => StartCoroutine(StepThroughDialog(m_dialogue));

    /* go through one 'screen' of dialogue at a time, waiting for player input to continue */
    private IEnumerator StepThroughDialog(ScriptableDialogue scriptableDialogue)
    {
        allDone = false;    // signal dialogue is not done yet

        Speech last = scriptableDialogue.Dialogue[scriptableDialogue.Dialogue.Length-1];
        foreach (Speech speech in scriptableDialogue.Dialogue)
        {
            continueDialogue = false;
            animator.SetBool(_animIDTalking2, speech.important);    // assign different animation for 'important' information
            yield return WriteText(speech.dialogue);                // wait for text to be written
            if (!speech.Equals(last))                               // wait for player input to continue
                yield return new WaitUntil(() => continueDialogue);
        }

        allDone = true;     // signal dialogue is done 
    }

    /* end the dialogue interaction */
    private void EndInteraction()
    {
        talking = false;
        dialogueContainer.SetActive(false);             // remove UI
        textLabel.text = string.Empty;                  // ensure text label is empty
        animator.SetBool(_animIDTalking, false);        // end animations
        animator.SetBool(_animIDTalking2, false);
        if (writingCo != null)                          // make sure writing coroutine stops
            StopCoroutine(writingCo);
        player.controls = true;                         // give control back to player
        CameraController.Instance.SetInput(true);
    }

    /* write text to screen with 'typewriter' effect */
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