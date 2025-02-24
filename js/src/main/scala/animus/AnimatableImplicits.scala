package animus

trait AnimatableImplicits {
  implicit val animatableDouble: Animatable[Double] = new Animatable[Double] {
    override type Anim = Spring

    override def tick(anim: Spring, time: Double): Boolean = {
      anim.tick(time)
      anim.isDone
    }

    override def toAnim(value: Double, configure: Spring => Spring): Spring =
      configure(Spring.fromValue(value))

    override def update(anim: Spring, newValue: Double): Unit = anim.setTarget(newValue)

    override def fromAnim(anim: Spring): Double = anim.position
  }

  implicit def animatableTuple[A, B](implicit a: Animatable[A], b: Animatable[B]): Animatable[(A, B)] =
    new Animatable[(A, B)] {
      override type Anim = (a.Anim, b.Anim)

      override def tick(anim: (a.Anim, b.Anim), time: Double): Boolean = {
        val d1 = a.tick(anim._1, time)
        val d2 = b.tick(anim._2, time)
        d1 && d2
      }

      override def toAnim(value: (A, B), configure: Spring => Spring): (a.Anim, b.Anim) =
        (a.toAnim(value._1, configure), b.toAnim(value._2, configure))

      override def update(anim: (a.Anim, b.Anim), newValue: (A, B)): Unit = {
        a.update(anim._1, newValue._1)
        b.update(anim._2, newValue._2)
      }

      override def fromAnim(anim: (a.Anim, b.Anim)): (A, B) = (a.fromAnim(anim._1), b.fromAnim(anim._2))
    }

  implicit def animatableTuple3[A, B, C](implicit
    a: Animatable[A],
    b: Animatable[B],
    c: Animatable[C]
  ): Animatable[(A, B, C)] =
    new Animatable[(A, B, C)] {
      override type Anim = (a.Anim, b.Anim, c.Anim)

      override def tick(anim: (a.Anim, b.Anim, c.Anim), time: Double): Boolean = {
        val d1 = a.tick(anim._1, time)
        val d2 = b.tick(anim._2, time)
        val d3 = c.tick(anim._3, time)
        d1 && d2 && d3
      }

      override def toAnim(value: (A, B, C), configure: Spring => Spring): (a.Anim, b.Anim, c.Anim) =
        (a.toAnim(value._1, configure), b.toAnim(value._2, configure), c.toAnim(value._3, configure))

      override def update(anim: (a.Anim, b.Anim, c.Anim), newValue: (A, B, C)): Unit = {
        a.update(anim._1, newValue._1)
        b.update(anim._2, newValue._2)
        c.update(anim._3, newValue._3)
      }

      override def fromAnim(anim: (a.Anim, b.Anim, c.Anim)): (A, B, C) =
        (a.fromAnim(anim._1), b.fromAnim(anim._2), c.fromAnim(anim._3))
    }

  implicit def animatableIterable[A](implicit a: Animatable[A]): Animatable[Iterable[A]] =
    new Animatable[Iterable[A]] {
      override type Anim = Iterable[a.Anim]

      override def tick(anim: Iterable[a.Anim], time: Double): Boolean =
        anim.map(a.tick(_, time)).reduce(_ && _)

      override def toAnim(value: Iterable[A], configure: Spring => Spring): Iterable[a.Anim] =
        value.map(v => a.toAnim(v, configure))

      override def update(anim: Iterable[a.Anim], newValue: Iterable[A]): Unit =
        anim zip newValue foreach { case (an, nv) => a.update(an, nv) }

      override def fromAnim(anim: Iterable[a.Anim]): Iterable[A] =
        anim.map(a.fromAnim)
    }
}
