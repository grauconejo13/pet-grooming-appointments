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
  createClient,
  createPet,
  getAppointment,
  getGroomers,
  getServices,
  updateAppointmentStatus,
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
  const [profileState, setProfileState] = useState({ status: 'idle', message: '' });
  const [appointmentLookupId, setAppointmentLookupId] = useState('');
  const [lookupState, setLookupState] = useState({ status: 'idle', message: '' });
  const [foundAppointment, setFoundAppointment] = useState(null);
  const [petName, setPetName] = useState('');
  const [clientForm, setClientForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    species: 'Dog',
    breed: '',
    size: 'Small'
  });

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

  async function handleAppointmentLookup(event) {
    event.preventDefault();

    if (!hasApi()) {
      setLookupState({
        status: 'error',
        message: 'Connect the backend API to look up a live appointment.'
      });
      return;
    }

    if (!appointmentLookupId) {
      setLookupState({ status: 'error', message: 'Enter an appointment number first.' });
      return;
    }

    setLookupState({ status: 'loading', message: 'Looking up your appointment…' });

    try {
      const appointment = await getAppointment(appointmentLookupId);
      setFoundAppointment(appointment);
      setLookupState({ status: 'success', message: 'Appointment found.' });
    } catch (error) {
      setFoundAppointment(null);
      setLookupState({ status: 'error', message: error.message });
    }
  }

  async function handleCancelAppointment() {
    if (!foundAppointment) return;

    setLookupState({ status: 'loading', message: 'Cancelling appointment…' });

    try {
      const updated = await updateAppointmentStatus(foundAppointment.id, 'CANCELLED');
      setFoundAppointment(updated);
      setLookupState({ status: 'success', message: 'Appointment cancelled.' });
    } catch (error) {
      setLookupState({ status: 'error', message: error.message });
    }
  }

  async function handleProfileSubmit(event) {
    event.preventDefault();

    if (!hasApi()) {
      setProfileState({
        status: 'error',
        message: 'Connect the backend API before creating a real client and pet.'
      });
      return;
    }

    setProfileState({ status: 'loading', message: 'Creating your pet profile…' });

    try {
      const client = await createClient({
        firstName: clientForm.firstName,
        lastName: clientForm.lastName,
        email: clientForm.email,
        phone: clientForm.phone
      });

      const pet = await createPet({
        clientId: client.id,
        name: petName,
        species: clientForm.species,
        breed: clientForm.breed || null,
        birthDate: null,
        size: clientForm.size,
        careNotes: null
      });

      setPetId(String(pet.id));
      setProfileState({
        status: 'success',
        message: `${pet.name} is ready to book. Pet #${pet.id} is selected below.`
      });

      document.querySelector('#booking')?.scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
      setProfileState({ status: 'error', message: error.message });
    }
  }

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
          <a href="#my-appointments" className="ghost-button">My appointments</a>
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

      <section className="onboarding-panel" id="pet-profile">
        <div className="section-heading onboarding-heading">
          <div>
            <span className="eyebrow"><PawPrint size={15} /> New client</span>
            <h2>Tell us who is arriving.</h2>
          </div>
          <p>Create the owner and pet records once, then carry the pet straight into booking.</p>
        </div>

        <form className="profile-card" onSubmit={handleProfileSubmit}>
          <div className="form-row">
            <label>
              First name
              <input
                value={clientForm.firstName}
                onChange={(event) => setClientForm({ ...clientForm, firstName: event.target.value })}
                required
              />
            </label>
            <label>
              Last name
              <input
                value={clientForm.lastName}
                onChange={(event) => setClientForm({ ...clientForm, lastName: event.target.value })}
                required
              />
            </label>
          </div>

          <div className="form-row">
            <label>
              Email
              <input
                type="email"
                value={clientForm.email}
                onChange={(event) => setClientForm({ ...clientForm, email: event.target.value })}
                required
              />
            </label>
            <label>
              Phone
              <input
                value={clientForm.phone}
                onChange={(event) => setClientForm({ ...clientForm, phone: event.target.value })}
                required
              />
            </label>
          </div>

          <div className="profile-divider"><span>Pet details</span></div>

          <div className="form-row">
            <label>
              Pet name
              <input
                value={petName}
                onChange={(event) => setPetName(event.target.value)}
                placeholder="Mochi"
                required
              />
            </label>
            <label>
              Species
              <select
                value={clientForm.species}
                onChange={(event) => setClientForm({ ...clientForm, species: event.target.value })}
              >
                <option>Dog</option>
                <option>Cat</option>
                <option>Other</option>
              </select>
            </label>
          </div>

          <div className="form-row">
            <label>
              Breed
              <input
                value={clientForm.breed}
                onChange={(event) => setClientForm({ ...clientForm, breed: event.target.value })}
                placeholder="Optional"
              />
            </label>
            <label>
              Size
              <select
                value={clientForm.size}
                onChange={(event) => setClientForm({ ...clientForm, size: event.target.value })}
              >
                <option>Small</option>
                <option>Medium</option>
                <option>Large</option>
                <option>Extra Large</option>
              </select>
            </label>
          </div>

          <button
            type="submit"
            className="primary-button wide"
            disabled={profileState.status === 'loading'}
          >
            {profileState.status === 'loading' ? 'Creating profile…' : 'Create client & pet'}
          </button>

          {profileState.message && (
            <p
              className={`form-feedback ${profileState.status}`}
              role={profileState.status === 'error' ? 'alert' : 'status'}
            >
              {profileState.status === 'success' && <CheckCircle2 size={16} />}
              {profileState.message}
            </p>
          )}
        </form>
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

          <div className="selected-pet">
            <span>Pet</span>
            <strong>{petName || 'Create a pet profile above'}</strong>
            <small>{petId ? `Pet #${petId} selected` : 'No pet selected yet'}</small>
          </div>

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

      <section className="appointment-lookup" id="my-appointments">
        <div className="lookup-copy">
          <span className="eyebrow"><CalendarDays size={15} /> My appointments</span>
          <h2>Find a booking by confirmation number.</h2>
          <p>
            Enter the appointment number from your booking confirmation to review
            its time and current status.
          </p>
        </div>

        <div className="lookup-card">
          <form className="lookup-form" onSubmit={handleAppointmentLookup}>
            <label>
              Appointment number
              <input
                type="number"
                min="1"
                value={appointmentLookupId}
                onChange={(event) => setAppointmentLookupId(event.target.value)}
                placeholder="e.g. 42"
                required
              />
            </label>
            <button
              type="submit"
              className="primary-button"
              disabled={lookupState.status === 'loading'}
            >
              {lookupState.status === 'loading' ? 'Checking…' : 'Find appointment'}
            </button>
          </form>

          {foundAppointment && (
            <div className="appointment-result">
              <div className="result-heading">
                <div>
                  <small>Appointment #{foundAppointment.id}</small>
                  <strong>{foundAppointment.status}</strong>
                </div>
                <span className={`status-pill status-${foundAppointment.status.toLowerCase()}`}>
                  {foundAppointment.status}
                </span>
              </div>

              <dl>
                <div>
                  <dt>Starts</dt>
                  <dd>{new Date(foundAppointment.startsAt).toLocaleString()}</dd>
                </div>
                <div>
                  <dt>Pet</dt>
                  <dd>#{foundAppointment.petId}</dd>
                </div>
                <div>
                  <dt>Groomer</dt>
                  <dd>#{foundAppointment.groomerId}</dd>
                </div>
                <div>
                  <dt>Service</dt>
                  <dd>#{foundAppointment.serviceId}</dd>
                </div>
              </dl>

              {['PENDING', 'CONFIRMED'].includes(foundAppointment.status) && (
                <button
                  type="button"
                  className="danger-button"
                  onClick={handleCancelAppointment}
                  disabled={lookupState.status === 'loading'}
                >
                  Cancel appointment
                </button>
              )}
            </div>
          )}

          {lookupState.message && lookupState.status !== 'idle' && (
            <p
              className={`form-feedback ${lookupState.status}`}
              role={lookupState.status === 'error' ? 'alert' : 'status'}
            >
              {lookupState.status === 'success' && <CheckCircle2 size={16} />}
              {lookupState.message}
            </p>
          )}
        </div>
      </section>

      <footer>
        <span><PawPrint size={16} /> Paw & Polish</span>
        <span>Spring Boot API · React client</span>
      </footer>
    </main>
  );
}
