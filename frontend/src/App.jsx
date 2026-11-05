import { useEffect, useMemo, useState } from 'react';
import {
  CalendarDays,
  CheckCircle2,
  Clock3,
  Heart,
  PawPrint,
  Scissors,
  ShieldCheck,
  Sparkles
} from 'lucide-react';
import {
  createAppointment,
  getGroomers,
  getServices,
  hasApi
} from './api';

const demoServices = [
  {
    id: 1,
    name: 'Full Groom',
    description: 'Bath, brush, haircut, nail trim, ear clean.',
    durationMinutes: 90,
    price: 85,
    active: true
  },
  {
    id: 2,
    name: 'Freshen Up',
    description: 'Bath, blow dry, brush out, and nail trim.',
    durationMinutes: 60,
    price: 55,
    active: true
  },
  {
    id: 3,
    name: 'Puppy First Visit',
    description: 'A gentle introduction to grooming for little first-timers.',
    durationMinutes: 45,
    price: 40,
    active: true
  }
];

const demoGroomers = [
  { id: 1, firstName: 'Maya', lastName: 'Chen', active: true },
  { id: 2, firstName: 'Alex', lastName: 'Rivera', active: true },
  { id: 3, firstName: 'Sam', lastName: 'Patel', active: true }
];

function money(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(value);
}

function addMinutes(localDateTime, minutes) {
  const date = new Date(localDateTime);
  date.setMinutes(date.getMinutes() + minutes);
  const pad = (value) => String(value).padStart(2, '0');
  return [
    date.getFullYear(),
    '-',
    pad(date.getMonth() + 1),
    '-',
    pad(date.getDate()),
    'T',
    pad(date.getHours()),
    ':',
    pad(date.getMinutes()),
    ':00'
  ].join('');
}

export default function App() {
  const [services, setServices] = useState(demoServices);
  const [groomers, setGroomers] = useState(demoGroomers);
  const [selectedService, setSelectedService] = useState(1);
  const [selectedGroomer, setSelectedGroomer] = useState(1);
  const [petId, setPetId] = useState('');
  const [date, setDate] = useState('');
  const [time, setTime] = useState('10:30');
  const [bookingState, setBookingState] = useState({ status: 'idle', message: '' });

  useEffect(() => {
    if (!hasApi()) return;

    Promise.all([getServices(), getGroomers()])
      .then(([serviceData, groomerData]) => {
        if (serviceData.length) {
          setServices(serviceData);
          setSelectedService(serviceData[0].id);
        }
        if (groomerData.length) {
          setGroomers(groomerData);
          setSelectedGroomer(groomerData[0].id);
        }
      })
      .catch(() => {
        // Keep demo data so preview deployments always remain presentable.
      });
  }, []);

  const service = useMemo(
    () => services.find((item) => item.id === selectedService) ?? services[0],
    [services, selectedService]
  );

  const groomer = useMemo(
    () => groomers.find((item) => item.id === selectedGroomer) ?? groomers[0],
    [groomers, selectedGroomer]
  );

  async function handleBooking(event) {
    event.preventDefault();

    if (!hasApi()) {
      setBookingState({
        status: 'error',
        message: 'Preview mode is active. Add VITE_API_BASE_URL in Vercel to send real bookings.'
      });
      return;
    }

    if (!petId || !date || !time || !service) {
      setBookingState({ status: 'error', message: 'Choose a pet, date, and time first.' });
      return;
    }

    const startsAt = `${date}T${time}:00`;
    setBookingState({ status: 'loading', message: 'Reserving your appointment…' });

    try {
      const appointment = await createAppointment({
        petId: Number(petId),
        groomerId: selectedGroomer,
        serviceId: selectedService,
        startsAt,
        endsAt: addMinutes(startsAt, service.durationMinutes),
        clientNote: null
      });

      setBookingState({
        status: 'success',
        message: `Appointment #${appointment.id} is booked and pending confirmation.`
      });
    } catch (error) {
      setBookingState({ status: 'error', message: error.message });
    }
  }

  return (
    <main className="page-shell">
      <nav className="topbar">
        <a className="brand" href="#top" aria-label="Paw and Polish home">
          <span className="brand-mark"><PawPrint size={20} /></span>
          <span>
            <strong>Paw & Polish</strong>
            <small>Pet Grooming Studio</small>
          </span>
        </a>
        <div className="nav-links">
          <a href="#services">Services</a>
          <a href="#booking">Book</a>
          <button type="button" className="ghost-button">My appointments</button>
        </div>
      </nav>

      <section className="hero" id="top">
        <div className="hero-copy">
          <span className="eyebrow"><Sparkles size={15} /> Gentle care, tidy paws</span>
          <h1>Grooming that feels a little less like an errand.</h1>
          <p>
            Choose a service, pick a groomer, and reserve a calm appointment
            built around your pet.
          </p>
          <div className="hero-actions">
            <a href="#booking" className="primary-button">Book an appointment</a>
            <a href="#services" className="text-link">Browse services</a>
          </div>
          <div className="trust-row">
            <span><Heart size={16} /> Pet-first handling</span>
            <span><ShieldCheck size={16} /> Clear appointment status</span>
            <span><Clock3 size={16} /> No double-booking</span>
          </div>
        </div>

        <div className="hero-card">
          <div className="hero-card-top">
            <span>Next visit</span>
            <span className="status-pill">Confirmed</span>
          </div>
          <div className="pet-orbit">
            <div className="orbit-ring"></div>
            <div className="pet-badge"><PawPrint size={42} /></div>
          </div>
          <div className="appointment-preview">
            <strong>Saturday, 10:30 AM</strong>
            <span>Full Groom · 90 min</span>
            <span>with Maya</span>
          </div>
        </div>
      </section>

      <section className="section" id="services">
        <div className="section-heading">
          <div>
            <span className="eyebrow"><Scissors size={15} /> Services</span>
            <h2>Pick the right level of fluff management.</h2>
          </div>
          <p>Simple pricing, clear timing, no mystery add-ons in the preview.</p>
        </div>

        <div className="service-grid">
          {services.map((item) => (
            <button
              type="button"
              key={item.id}
              className={`service-card ${selectedService === item.id ? 'selected' : ''}`}
              onClick={() => setSelectedService(item.id)}
            >
              <div className="service-icon"><Scissors size={20} /></div>
              <h3>{item.name}</h3>
              <p>{item.description}</p>
              <div className="service-meta">
                <span>{item.durationMinutes} min</span>
                <strong>{money(item.price)}</strong>
              </div>
            </button>
          ))}
        </div>
      </section>

      <section className="booking-panel" id="booking">
        <div className="booking-intro">
          <span className="eyebrow"><CalendarDays size={15} /> Book online</span>
          <h2>Build your visit.</h2>
          <p>
            Choose a groomer and time, then send the appointment directly to
            the Spring booking API.
          </p>

          <div className="booking-summary">
            <span>Selected service</span>
            <strong>{service?.name ?? 'Choose a service'}</strong>
            <small>{service ? `${service.durationMinutes} min · ${money(service.price)}` : ''}</small>
          </div>
        </div>

        <form className="booking-card" onSubmit={handleBooking}>
          <label>
            Groomer
            <select
              value={selectedGroomer}
              onChange={(event) => setSelectedGroomer(Number(event.target.value))}
            >
              {groomers.map((item) => (
                <option key={item.id} value={item.id}>
                  {item.firstName} {item.lastName}
                </option>
              ))}
            </select>
          </label>

          <label>
            Pet ID
            <input
              type="number"
              min="1"
              value={petId}
              onChange={(event) => setPetId(event.target.value)}
              placeholder="e.g. 7"
              required
            />
          </label>

          <div className="form-row">
            <label>
              Date
              <input
                type="date"
                value={date}
                onChange={(event) => setDate(event.target.value)}
                required
              />
            </label>
            <label>
              Time
              <select value={time} onChange={(event) => setTime(event.target.value)}>
                <option value="09:00">09:00</option>
                <option value="10:30">10:30</option>
                <option value="13:00">13:00</option>
                <option value="15:30">15:30</option>
              </select>
            </label>
          </div>

          <button
            type="submit"
            className="primary-button wide"
            disabled={bookingState.status === 'loading'}
          >
            {bookingState.status === 'loading'
              ? 'Booking…'
              : `Book with ${groomer?.firstName ?? 'groomer'}`}
          </button>

          {bookingState.message && (
            <p
              className={`form-feedback ${bookingState.status}`}
              role={bookingState.status === 'error' ? 'alert' : 'status'}
            >
              {bookingState.status === 'success' && <CheckCircle2 size={16} />}
              {bookingState.message}
            </p>
          )}

          {!hasApi() && (
            <small className="form-note">
              Preview mode is active until VITE_API_BASE_URL points to the Spring API.
            </small>
          )}
        </form>
      </section>

      <footer>
        <span><PawPrint size={16} /> Paw & Polish</span>
        <span>Spring Boot API · React client</span>
      </footer>
    </main>
  );
}
