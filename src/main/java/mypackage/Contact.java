package mypackage;

import processing.core.*;

public class Contact {

    Particle p ;
    Missile m ;

    float c ;

    PVector contactNormal;
    PApplet parent;



    public Contact (PApplet p, Particle p1, Missile m, float c, PVector contactNormal) {
        parent = p;
        this.p = p1 ;
        this.m = m ;
        this.c = c ;
        this.contactNormal = contactNormal ;
    }


    void resolve () {
        resolveVelocity() ;
    }

    float calculateSeparatingVelocity() {
        PVector relativeVelocity = p.velocity.copy() ;
        relativeVelocity.sub(m.impulse) ;
        return relativeVelocity.dot(contactNormal) ;
    }

    void resolveVelocity()  {
        float separatingVelocity = calculateSeparatingVelocity() ;

        float newSepVelocity = -separatingVelocity * c ;

        float deltaVelocity = newSepVelocity - separatingVelocity ;

        float totalInverseMass = p.invMass ;
        totalInverseMass += m.INV_MASS ;

        float impulse = deltaVelocity / totalInverseMass * 2 ;

        PVector impulsePerIMass = contactNormal.copy() ;
        impulsePerIMass.mult(impulse) ;

        PVector pImpulse = impulsePerIMass.copy() ;
        pImpulse.mult(p.invMass) ;

        p.velocity.add(pImpulse);
    }
}