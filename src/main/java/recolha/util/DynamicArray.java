package recolha.util;

import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Lista dinâmica simples, baseada num vetor que duplica de tamanho quando fica cheio.
 *
 * <p>O enunciado não permitia usar a Java Collections Framework. Na versão original cada classe
 * tinha o seu próprio vetor, contador e método {@code raise...()}, o que repetia a mesma lógica
 * (e os mesmos erros) em sete sítios diferentes. Esta classe concentra essa lógica num só local.</p>
 *
 * @param <T> tipo dos elementos guardados
 * @author Francisco Miguel Pereira Oliveira
 */
public class DynamicArray<T> {

    private static final int INITIAL_CAPACITY = 8;

    private Object[] items;
    private int size;

    /** Cria uma lista vazia. */
    public DynamicArray() {
        this.items = new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    /** @return número de elementos guardados */
    public int size() {
        return this.size;
    }

    /** @return {@code true} se a lista não tiver elementos */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Devolve o elemento numa posição.
     *
     * @param index posição (0 a {@code size() - 1})
     * @return o elemento guardado
     * @throws IndexOutOfBoundsException se a posição não existir
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index, this.size);
        return (T) this.items[index];
    }

    /**
     * Substitui o elemento numa posição.
     *
     * @param index posição a alterar
     * @param item novo elemento
     */
    public void set(int index, T item) {
        checkIndex(index, this.size);
        this.items[index] = item;
    }

    /**
     * Acrescenta um elemento ao fim da lista.
     *
     * @param item elemento a acrescentar
     */
    public void add(T item) {
        ensureCapacity(this.size + 1);
        this.items[this.size++] = item;
    }

    /**
     * Insere um elemento numa posição, deslocando os seguintes para a direita.
     *
     * @param index posição de inserção (0 a {@code size()})
     * @param item elemento a inserir
     */
    public void insertAt(int index, T item) {
        checkIndex(index, this.size + 1);
        ensureCapacity(this.size + 1);
        System.arraycopy(this.items, index, this.items, index + 1, this.size - index);
        this.items[index] = item;
        this.size++;
    }

    /**
     * Remove o elemento numa posição.
     *
     * @param index posição a remover
     * @return o elemento removido
     */
    public T removeAt(int index) {
        T removed = get(index);
        System.arraycopy(this.items, index + 1, this.items, index, this.size - index - 1);
        this.items[--this.size] = null;
        return removed;
    }

    /**
     * Procura um elemento usando {@code equals}.
     *
     * @param item elemento a procurar
     * @return a posição da primeira ocorrência ou -1
     */
    public int indexOf(Object item) {
        for (int i = 0; i < this.size; i++) {
            if (item == null ? this.items[i] == null : item.equals(this.items[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param item elemento a procurar
     * @return {@code true} se o elemento existir na lista
     */
    public boolean contains(Object item) {
        return indexOf(item) != -1;
    }

    /**
     * Devolve o primeiro elemento que satisfaz uma condição.
     *
     * @param condition condição a verificar
     * @return o elemento encontrado ou {@code null}
     */
    public T find(Predicate<? super T> condition) {
        for (int i = 0; i < this.size; i++) {
            T item = get(i);
            if (condition.test(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Copia os elementos para um vetor do tamanho exato da lista.
     *
     * <p>Exemplo: {@code containers.toArray(Container[]::new)}.</p>
     *
     * @param factory cria um vetor do tipo pretendido com o tamanho indicado
     * @return um vetor novo; alterá-lo não afeta a lista
     */
    public T[] toArray(IntFunction<T[]> factory) {
        T[] result = factory.apply(this.size);
        for (int i = 0; i < this.size; i++) {
            result[i] = get(i);
        }
        return result;
    }

    private void ensureCapacity(int required) {
        if (required > this.items.length) {
            Object[] bigger = new Object[Math.max(required, this.items.length * 2)];
            System.arraycopy(this.items, 0, bigger, 0, this.size);
            this.items = bigger;
        }
    }

    private static void checkIndex(int index, int limit) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException("Posição " + index + " fora do intervalo [0, " + limit + "[");
        }
    }
}
