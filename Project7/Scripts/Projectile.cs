using UnityEngine;

public class Projectile : MonoBehaviour
{
    [SerializeField]
    private float m_speed = 40f;
    [SerializeField]
    private float m_timeToDestroy = 3f;
    [SerializeField]
    private GameObject[] m_strikeEffects;

    [HideInInspector]
    public float damage { get; set; }

    public Vector3 target { get; set; }
    public bool hit { get; set; }

    private void OnEnable()
    {
        Destroy(gameObject, m_timeToDestroy);
    }
    
    private void FixedUpdate()
    {
        transform.position = Vector3.MoveTowards(transform.position, target, m_speed * Time.deltaTime);
        if (!hit && Vector3.Distance(transform.position, target) < .01f)
        {
            Destroy(gameObject);
        }
    }

    void OnTriggerEnter(Collider collider)
    {
        if (collider.GetType() != typeof(CapsuleCollider)) return; 

        var character = collider.gameObject.GetComponentInParent<Character>();
        if (character)
        {
            character.SendMessage("Damage", damage);
        }
        //ContactPoint contact = collision.GetContact(0);
        /* effects when projectile strikes something
        if (m_strikeEffects.Length > 0)
        {
            foreach (var effect in m_strikeEffects)
            {
                Instantiate(effect, transform.position, transform.rotation);
            }
        }
        */
        // If we want to paint a 2D decal on a surface use this
        //GameObject.Instantiate(projectileDecal, contact.point + contact.normal * 0.0001f, Quaternion.LookRotation(contact.normal));
        Destroy(gameObject);
    }
}