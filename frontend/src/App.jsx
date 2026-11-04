import { useEffect, useMemo, useState } from 'react';
import {
  CalendarDays,
  Clock3,
  Heart,
  PawPrint,
  Scissors,
  ShieldCheck,
  Sparkles
} from 'lucide-react';

const apiBase = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '');

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

export default function App() {
  const [services, setServices] = useState(demoServices);
  const [groomers, setGroomers] = useState(demoGroomers);
  const [selectedService, setSelectedService] = useState(1);
  const [selectedGroomer, setSelectedGroomer] = useState(1);

  useEffect(() => {
    if (!apiBase) return;

    Promise.all([
      fetch(`${apiBase}/api/services`).then((response) => {
        if (!response.ok) throw new Error('services unavailable');
        return response.json();
      }),
      fetch(`${apiBase}/api/groomers`).then((response) => {
        if (!response.ok) throw new Error('groomers unavailable');
        return response.json();
      })
    ])
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
          <span className="eyebrow"><CalendarDays size={15} /> Booking preview</span>
          <h2>Build your visit.</h2>
          <p>
            This first frontend commit is preview-ready. When a backend URL is
            configured, the service and groomer choices load from the Spring API.
          </p>

          <div className="booking-summary">
            <span>Selected service</span>
            <strong>{service?.name ?? 'Choose a service'}</strong>
            <small>{service ? `${service.durationMinutes} min · ${money(service.price)}` : ''}</small>
          </div>
        </div>

        <div className="booking-card">
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

          <div className="form-row">
            <label>
              Date
              <input type="date" />
            </label>
            <label>
              Time
              <select defaultValue="10:30">
                <option>09:00</option>
                <option>10:30</option>
                <option>13:00</option>
                <option>15:30</option>
              </select>
            </label>
          </div>

          <label>
            Pet name
            <input type="text" placeholder="Mochi" />
          </label>

          <button type="button" className="primary-button wide">
            Continue with {groomer?.firstName ?? 'groomer'}
          </button>
          <small className="form-note">
            Preview mode only. Submission wiring comes in the next frontend milestone.
          </small>
        </div>
      </section>

      <footer>
        <span><PawPrint size={16} /> Paw & Polish</span>
        <span>Spring Boot API · React client</span>
      </footer>
    </main>
  );
}
