import { isPlainObject, values, isNumber, isBoolean, isArray, isString } from 'lodash';
import { Seq } from 'Utils/IterableUtils';

/**
 * Validate and parse JSON strings into TypeScript/JavaScript objects.
 *
 * Inspired by Elm's Json.Decode library (https://guide.elm-lang.org/interop/json.html).
 *
 * This module exposes:
 * - primitive decoders (`string`, `number`, `boolean`, etc)
 * - higher-order decoders for creating more sophisticated decoders (`oneOf`,
 *   `field`, etc)
 * - a function for using the decoders (`decode`)
 *
 * In most cases, a decoder will be used to describe data we expect to receive
 * from a web service to ensure it conforms to types resident in the client
 * application. If the data does not conform, an error will be thrown describing
 * the first encountered problem.
 */
export type Decoder<T> = (t: any) => Result<T>;


// Decoder return types
// --------------------

type Ok<T> = {
  status: 'ok';
  value: T;
}

type Err = {
  status: 'err';
  value: any;
  expected: string;
  context?: string;
}

type Result<T> = Ok<T> | Err;

function ok<T>(value: T): Ok<T> {
  return { status: 'ok', value };
}

function err(value: any, expected: string, context?: string): Err {
  return { status: 'err', value, expected, context};
}


// primitives
// ----------

export function string(t: any): Result<string> {
  return isString(t)
    ? ok(t)
    : err(t, 'a string');
}

export function number(t: any): Result<number> {
  return isNumber(t)
    ? ok(t)
    : err(t, 'a number');
}

export function boolean(t: any): Result<boolean> {
  return isBoolean(t)
    ? ok(t)
    : err(t, 'a boolean');
}

export function nullValue(t: any): Result<null> {
  return t === null
    ? ok(t)
    : err(t, 'null');
}


// higher order decoders
// ---------------------

// Expect a specific value
export function constant<T>(value: T) {
  return function constantGuard(t: any): Result<T> {
    return t === value
      ? ok(t)
      : err(t, JSON.stringify(value))
  }
}

// Expect an object with string keys and values of a specific type
export function objectOf<T>(decoder: Decoder<T>) {
  return function objectOfDecoder(t: any): Result<Record<string, T>> {
    if (!isPlainObject(t)) return err(t, 'an object');
    const e = values(t).map(decoder).find(r => r.status === 'err') as Err;
    return e == null
      ? ok(t)
      : err(e.value, `an object where every field is ${e.expected}`);
  }
}

// Expect an array with elements of a specific type
export function arrayOf<T>(decoder: Decoder<T>) {
  return function arrayOfDecoder(t: any): Result<Array<T>> {
    if (!isArray(t)) return err(t, 'an array');
    const e = t.map(decoder).find(r => r.status === 'err') as Err;
    return e == null
      ? ok(t)
      : err(e.value, `an array where every element is ${e.expected}`);
  }
}

// Expect an object with a specific field whose value is of a specific type.
// Additional fields are ok and will be ignored.
// `field` decoders can be combined using `combine` to describe more properties
// of an object.
export function field<T, S extends string>(fieldName: S, decoder: Decoder<T>) {
  return function fieldDecoder(t: any): Result<{ [K in S]: T }> {
    if (!(isPlainObject(t))) return err(t, `an object`);
    const r = decoder(t[fieldName]);
    return r.status === 'ok'
      ? ok(t)
      : err(r.value, r.expected, `.${fieldName}${r.context || ''}`);
  }
}

// Expect a value of a specific type, or `undefined`
export function optional<T>(decoder: Decoder<T>) {
  return function optionalGuard(t?: any): Result<T | undefined> {
    return t === undefined
      ? ok(t)
      : decoder(t);
  }
}

// Combine multiple decoders such that all must return Ok
export function combine<T, S>(decoder1: Decoder<T>, decoder2: Decoder<S>): Decoder<T & S>;
export function combine<T, S, R>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>): Decoder<T & S & R>;
export function combine<T, S, R, Q>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>): Decoder<T & S & R & Q>;
export function combine<T, S, R, Q, P>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>): Decoder<T & S & R & Q & P>;
export function combine<T, S, R, Q, P, O>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>): Decoder<T & S & R & Q & P & O>;
export function combine<T, S, R, Q, P, O, N>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>): Decoder<T & S & R & Q & P & O & N>;
export function combine<T, S, R, Q, P, O, N, M>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>, decoder8: Decoder<M>): Decoder<T & S & R & Q & P & O & N & M>;
export function combine<T, S, R, Q, P, O, N, M, L>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>, decoder8: Decoder<M>, decoder9: Decoder<L>): Decoder<T & S & R & Q & P & O & N & M & L>;
export function combine<T, S, R, Q, P, O, N, M, L, K>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>, decoder8: Decoder<M>, decoder9: Decoder<L>, decoder10: Decoder<K>): Decoder<T & S & R & Q & P & O & N & M & L & K>;
export function combine(...decoders: any[]) {
  return function combineDecoder(t: any) {
    return Seq.from(decoders).map(d => d(t)).find(r => r.status === 'err') || ok(t);
  }
}

// Combine multiple decoders such that at least one must return Ok
export function oneOf<T, S>(decoder1: Decoder<T>, decoder2: Decoder<S>): Decoder<T | S>;
export function oneOf<T, S, R>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>): Decoder<T | S | R>;
export function oneOf<T, S, R, Q>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>): Decoder<T | S | R | Q>;
export function oneOf<T, S, R, Q, P>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>): Decoder<T | S | R | Q | P>;
export function oneOf<T, S, R, Q, P, O>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>): Decoder<T | S | R | Q | P | O>;
export function oneOf<T, S, R, Q, P, O, N>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>): Decoder<T | S | R | Q | P | O | N>;
export function oneOf<T, S, R, Q, P, O, N, M>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>, decoder8: Decoder<M>): Decoder<T | S | R | Q | P | O | N | M>;
export function oneOf<T, S, R, Q, P, O, N, M, L>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>, decoder8: Decoder<M>, decoder9: Decoder<L>): Decoder<T | S | R | Q | P | O | N | M | L>;
export function oneOf<T, S, R, Q, P, O, N, M, L, K>(decoder1: Decoder<T>, decoder2: Decoder<S>, decoder3: Decoder<R>, decoder4: Decoder<Q>, decoder5: Decoder<P>, decoder6: Decoder<O>, decoder7: Decoder<N>, decoder8: Decoder<M>, decoder9: Decoder<L>, decoder10: Decoder<K>): Decoder<T | S | R | Q | P | O | N | M | L | K>;
export function oneOf(...decoders: any[]) {
  return function oneOfGuard(t: any) {
    const results = Seq.from(decoders).map(d => d(t));
    return results.find(r => r.status === 'ok')
      ? ok(t)
      : err(t, `one of [ ${results.map(e => e.expected).join(', ')} ]`)
  }
}

// Ensure that a decoder is not evaluated until it is needed. This is useful for
// recursive types (like trees).
export function lazy<T>(decoderThunk: () => Decoder<T>) {
  return function lazyGuard(t: any): Result<T> {
    return decoderThunk()(t);
  }
}

// Decoder
// -------

// Run `decoder` on `jsonString`
export function decode<T>(decoder: Decoder<T>, jsonString: string): T {
  let t: any;
  try {
    t = JSON.parse(jsonString);
  }
  catch (error) {
    throw new Error("Provided JSON is not valid: " + error.message);
  }
  const r = decoder(t);
  if (r.status === 'err') {
    throw new Error(`Could not decode string: Expected ${r.expected}${r.context ? (' at _' + r.context) : ''}, but got ${JSON.stringify(r.value)}`);
  }
  return r.value;
}