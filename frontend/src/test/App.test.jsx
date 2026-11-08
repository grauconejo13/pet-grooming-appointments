import { render, screen } from '@testing-library/react';
import App from '../App';

describe('App smoke test', () => {
  it('renders the primary booking experience', () => {
    render(<App />);

    expect(
      screen.getByRole('heading', {
        name: /grooming that feels a little less like an errand/i
      })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { name: /pick the right level of fluff management/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { name: /build your visit/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { name: /find a booking by confirmation number/i })
    ).toBeInTheDocument();

    expect(screen.getByRole('link', { name: /my appointments/i })).toBeInTheDocument();
  });
});
