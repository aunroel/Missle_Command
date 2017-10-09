import processing.core.*;

public class Contact {
    // The two particles in contact
    Particle p ;
    Missile m ;

    // Coefficient of restitution
    float c ;

    // The direction of the contact (from p1's perspective)
    // Equivalent to normal of p1 - p2
    PVector contactNormal;
    PApplet parent;


    // Construct a new Contact from the given parameters
    public Contact (PApplet p, Particle p1, Missile m, float c, PVector contactNormal) {
        parent = p;
        this.p = p1 ;
        this.m = m ;
        this.c = c ;
        this.contactNormal = contactNormal ;
    }

    // Resolve this contact for velocity
    void resolve () {
        resolveVelocity() ;
    }

    // Calculate the separating velocity for this contact
    // This is just the simplified form of the closing velocity eqn
    float calculateSeparatingVelocity() {
        PVector relativeVelocity = p.velocity.copy() ;
        relativeVelocity.sub(m.impulse) ;
        return relativeVelocity.dot(contactNormal) ;
    }

    // Handle the impulse calculations for this collision
    void resolveVelocity()  {
        //Find the velocity in the direction of the contact
        float separatingVelocity = calculateSeparatingVelocity() ;

        // Calculate new separating velocity
        float newSepVelocity = -separatingVelocity * c ;

        // Now calculate the change required to achieve it
        float deltaVelocity = newSepVelocity - separatingVelocity ;

        // Apply change in velocity to each object in proportion inverse mass.
        // i.e. lower inverse mass (higher actual mass) means less change to vel.
        float totalInverseMass = p.invMass ;
        totalInverseMass += m.INV_MASS ;

        // Calculate impulse to apply
        float impulse = deltaVelocity / totalInverseMass * 2 ;

        // Find the amount of impulse per unit of inverse mass
        PVector impulsePerIMass = contactNormal.copy() ;
        impulsePerIMass.mult(impulse) ;

        // Calculate the p1 impulse
        PVector pImpulse = impulsePerIMass.copy() ;
        pImpulse.mult(p.invMass) ;

        // Calculate the p2 impulse
        // NB Negate this one because it is in the opposite direction
//        PVector p2Impulse = impulsePerIMass.copy() ;
//        p2Impulse.mult(-m.INV_MASS) ;

        // Apply impulses. They are applied in the direction of contact, proportional
        //  to inverse mass
        p.velocity.add(pImpulse) ;
//        m.velocity.add(p2Impulse) ;
    }
}