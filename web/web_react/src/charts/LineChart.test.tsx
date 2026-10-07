import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { colorSeries, LineChart, linePath, niceTicks, OTHER_COLOR, SERIES_COLORS } from './LineChart';

describe('niceTicks', () => {
  it('rounds the top up in 1, 2 or 5 steps', () => {
    expect(niceTicks(87)).toEqual([0, 50, 100]);
    expect(niceTicks(340)).toEqual([0, 100, 200, 300, 400]);
    expect(niceTicks(0)).toEqual([0, 1]);
  });
});

describe('linePath', () => {
  const times = [0, 1, 2, 3].map((t) => new Date(t * 1000));
  it('breaks the line where a sample is missing', () => {
    expect(linePath(times, [1, 2, null, 4], (t) => t.getTime() / 1000, (v) => v)).toBe('M0.0,1.0L1.0,2.0M3.0,4.0');
  });
});

describe('colorSeries', () => {
  const series = (n: number) => Array.from({ length: n }, (_, i) => ({ name: `s${i}`, values: [i] }));

  it('colors up to eight series in order', () => {
    expect(colorSeries(series(3)).map((s) => s.color)).toEqual(SERIES_COLORS.slice(0, 3));
  });

  it('folds the smallest past eight into Other', () => {
    const folded = colorSeries(series(10));
    expect(folded).toHaveLength(8);
    expect(folded.at(-1)).toEqual({ name: 'Other (3)', values: [0 + 1 + 2], color: OTHER_COLOR });
    expect(folded.slice(0, 7).map((s) => s.name)).toEqual(['s3', 's4', 's5', 's6', 's7', 's8', 's9']);
  });
});

describe('LineChart', () => {
  const times = [new Date('2026-10-07T10:00:00Z'), new Date('2026-10-07T10:01:00Z')];
  const series = [
    { name: 'login', values: [10, 20], color: SERIES_COLORS[0]! },
    { name: 'checkout', values: [5, null], color: SERIES_COLORS[1]! },
  ];

  it('has a legend for two series and reads values from the keyboard', async () => {
    render(<LineChart title="Users" times={times} series={series} />);
    expect(screen.getByRole('list', { name: 'Users series' })).toHaveTextContent('login');

    screen.getByRole('img', { name: /Users/ }).focus();
    await userEvent.keyboard('{ArrowRight}{ArrowRight}');
    const tooltip = screen.getByRole('status');
    expect(tooltip).toHaveTextContent('20login');
    expect(tooltip).toHaveTextContent('—checkout');
  });

  it('has a table view', async () => {
    render(<LineChart title="Users" times={times} series={series} />);
    await userEvent.click(screen.getByRole('button', { name: 'Show table' }));
    expect(screen.getAllByRole('row')).toHaveLength(3);
  });

  it('shows a message without data', () => {
    render(<LineChart title="Users" times={[]} series={[]} emptyMessage="Nothing yet" />);
    expect(screen.getByText('Nothing yet')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Show table' })).not.toBeInTheDocument();
  });
});
